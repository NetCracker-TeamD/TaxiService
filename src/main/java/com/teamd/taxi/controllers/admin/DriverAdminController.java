package com.teamd.taxi.controllers.admin;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.models.admin.DriverInfoResponseModel;
import com.teamd.taxi.models.admin.DriverPageModel;
import com.teamd.taxi.service.AdminPagesUtil;
import com.teamd.taxi.service.CarService;
import com.teamd.taxi.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 07-May-15.
 *
 * @author Nazar Dub
 */
@Controller
@RequestMapping("/admin")
public class DriverAdminController {

    private static final int DEFAULT_NUM_OF_RECORDS_ON_PAGE = 20;
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

//    private static final String MESSAGE_CAR_ID_NOT_EXIST = "admin.car.delete.nonexistent";
//    private static final String MESSAGE_SUCCESS_DELETE = "admin.car.delete.success";

    @Resource
    private Environment env;

    @Autowired
    private CarService carService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private AdminPagesUtil pagesUtil;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(DriverInfoResponseModel.class, new DriverInfoResponseModelSerializer())
            .registerTypeAdapter(Driver.class, new DriverSerializer())
            .registerTypeAdapter(Car.class, new CarSerializer())
            .registerTypeAdapter(FeatureListSerializer.featuresType, new FeatureListSerializer())
            .create();

    //URL example: drivers?page=1&order=last_name
    @RequestMapping(value = "/drivers", method = RequestMethod.GET)
    public String viewCars(@Valid DriverPageModel pageModel, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //TODO: Return 404 here or other error page
            return "404";
        }

        Sort sort = new Sort(new Sort.Order(DEFAULT_SORT_DIRECTION, pageModel.getOrder()));
        Page<Driver> drivers = driverService.getDrivers(new PageRequest(pageModel.getPage(), DEFAULT_NUM_OF_RECORDS_ON_PAGE, sort));
        model.addAttribute("page", drivers);

        ArrayList<Integer> pagination = pagesUtil.getPagination(pageModel.getPage(), drivers.getTotalPages());
        model.addAttribute("pagination", pagination);

        model.addAttribute("driverFeatures", driverService.getDriverFeatures());
        return "admin/drivers";
    }

    @RequestMapping(value = "/driver-info", method = RequestMethod.POST)
    @ResponseBody
    public Object getDriverInfo(@RequestParam(value = "id") Integer id) {
        Driver driver = driverService.getDriver(id);

        DriverInfoResponseModel response = new DriverInfoResponseModel(
                driverService.getDriverFeatures(),
                carService.getCarFeatures());
        response.setResultSuccess();
        response.setContent(driver);

        return gson.toJson(response);
    }

    private static class DriverSerializer implements JsonSerializer<Driver> {

        @Override
        public JsonElement serialize(Driver driver, Type type, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("lastName", driver.getLastName());
            json.addProperty("firstName", driver.getFirstName());
            json.addProperty("email", driver.getEmail());
            json.addProperty("phone", driver.getPhoneNumber());
            json.addProperty("sex", driver.getSex().toString());
            json.addProperty("enabled", driver.isEnabled());
            json.addProperty("work", driver.isAtWork());
            json.addProperty("license", driver.getLicense());
            json.add("car", context.serialize(driver.getCar()));
            json.add("features", context.serialize(driver.getFeatures(), FeatureListSerializer.featuresType));
            return json;
        }
    }

    private static class CarSerializer implements JsonSerializer<Car> {

        @Override
        public JsonElement serialize(Car car, Type type, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("model", car.getModel());
            json.addProperty("category", car.getCategory());
            json.addProperty("class", car.getCarClass().getClassName());
            json.addProperty("enabled", car.isEnabled());
            json.add("features", context.serialize(car.getFeatures(), FeatureListSerializer.featuresType));
            return json;
        }
    }

    private static class DriverInfoResponseModelSerializer implements JsonSerializer<DriverInfoResponseModel> {

        @Override
        public JsonElement serialize(DriverInfoResponseModel src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("result", src.getResult());
            json.add("content", context.serialize(src.getContent()));
            json.add("driverFeatures", context.serialize(src.getAllDriverFeatures(), FeatureListSerializer.featuresType));
            json.add("carFeatures", context.serialize(src.getAllCarFeatures(), FeatureListSerializer.featuresType));
            return json;
        }
    }

    private static class FeatureListSerializer extends TypeAdapter<List<Feature>> {

        public static final Type featuresType = new TypeToken<List<Feature>>() {
        }.getType();

        @Override
        public void write(JsonWriter out, List<Feature> value) throws IOException {
            out.beginObject();
            for (Feature f : value) {
                out.name(f.getId().toString());
                out.value(f.getName());
            }
            out.endObject();
        }

        @Override
        public List<Feature> read(JsonReader in) throws IOException {
            throw new UnsupportedOperationException();
        }
    }

}

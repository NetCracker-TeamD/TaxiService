package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.models.admin.AdminResponseModel;
import com.teamd.taxi.models.admin.CarModel;
import com.teamd.taxi.models.admin.CarsPageModel;
import com.teamd.taxi.service.AdminPagesUtil;
import com.teamd.taxi.service.CarService;
import com.teamd.taxi.validation.AdminCarValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 * Created on 02-May-15.
 *
 * @author Nazar Dub
 */
@Controller
@RequestMapping("/admin")
public class CarAdminController {

    private static final int DEFAULT_NUM_OF_RECORDS_ON_PAGE = 20;
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    private static final String MESSAGE_CAR_ID_NOT_EXIST = "admin.car.delete.nonexistent";
    private static final String MESSAGE_SUCCESS_DELETE = "admin.car.delete.success";
    private static final String MESSAGE_SUCCESS_CAR_CREATED = "admin.car.create.success";

    @Resource
    private Environment env;

    @Autowired
    private CarService carService;

    @Autowired
    private AdminPagesUtil pagesUtil;

    @Autowired
    private AdminCarValidator adminCarValidator;

    //URL example: cars?page=1&order=model
    @RequestMapping(value = "/cars", method = RequestMethod.GET)
    public String viewCars(@Valid CarsPageModel pageModel, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //TODO: Return 404 here or other error page
            return "404";
        }

        Sort sort = new Sort(new Sort.Order(DEFAULT_SORT_DIRECTION, pageModel.getOrder()));
        Page<Car> cars = carService.getCars(new PageRequest(pageModel.getPage(), DEFAULT_NUM_OF_RECORDS_ON_PAGE, sort));
        model.addAttribute("page", cars);

        ArrayList<Integer> pagination = pagesUtil.getPagination(pageModel.getPage(), cars.getTotalPages());
        model.addAttribute("pagination", pagination);

        model.addAttribute("carFeatures", carService.getCarFeatures());
        return "admin/cars";
    }

    //URL example: car-delete?id=5
    @RequestMapping(value = "/car-delete", method = RequestMethod.POST)
    @ResponseBody
    public AdminResponseModel<String> removeCar(@RequestParam(value = "id") Integer id) {
        AdminResponseModel<String> response = new AdminResponseModel<>();
        try {
            carService.removeCar(id);
            response.setResultSuccess();
            response.setContent(env.getRequiredProperty(MESSAGE_SUCCESS_DELETE));
        } catch (EmptyResultDataAccessException e) {
            response.setContent(env.getRequiredProperty(MESSAGE_CAR_ID_NOT_EXIST));
        }
        return response;
    }

    @RequestMapping(value = "/getForm_add_car", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Map<String, String>> showAddFormCar() {

        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> mapFeatures = null;

        List<Feature> features = carService.getCarFeatures();

        for (Feature feature : features) {
            mapFeatures = new HashMap<>();
            mapFeatures.put("id", feature.getId().toString());
            mapFeatures.put("feature_name", feature.getName());

            result.add(mapFeatures);

            mapFeatures = null;
        }

        return result;
    }

    @RequestMapping(value = "/create_car", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AdminResponseModel<Map<String, String>> createNewCar(@RequestBody CarModel carModel, BindingResult result) {
        System.out.println(carModel);

        AdminResponseModel<Map<String, String>> response = new AdminResponseModel<>();

        adminCarValidator.validate(carModel, result);
        if (result.hasErrors()) {

            response.setResultFailure();

            Map<String, String> mapError = new HashMap<>();

            for (FieldError fieldError : result.getFieldErrors()) {
                mapError.put(fieldError.getField(), env.getRequiredProperty(fieldError.getCode()));
            }
            response.setContent(mapError);
            return response;
        } else {

            List<Integer> listFeaturesId = new ArrayList<>();
            List<String> boolValuesFeatures = new ArrayList<>(carModel.getMapFeatures().values());
            Set<String> stringFeaturesIds = carModel.getMapFeatures().keySet();
            int i = 0;
            for (String stringFeatureId : stringFeaturesIds) {
                if (Boolean.parseBoolean(boolValuesFeatures.get(i))) {
                    listFeaturesId.add(Integer.parseInt(stringFeatureId));
                }
                i++;
            }

            List<Feature> features = new ArrayList<>();
            for (Integer featureId : listFeaturesId) {
                features.add(carService.getFeature(featureId));
            }

            Driver driver = null;
            if (!carModel.getDriverId().equals("-1")) {
                driver = carService.getDriver(Integer.parseInt(carModel.getDriverId()));
            }

            //initialization entity Car
            Car car = new Car();
//            car.setCarId(carService.getCountCars()+1);
            car.setFeatures(features);
            car.setEnabled(Boolean.parseBoolean(carModel.getEnable()));
            car.setModel(carModel.getModelName());
            car.setCategory(carModel.getCategory());
            car.setDriver(driver);
            car.setCarClass(carService.getCarClass(Integer.parseInt(carModel.getClassId())));

            carService.saveCar(car);

            response.setResultSuccess();
            response.setContent(new HashMap<String, String>() {{
                put("message", env.getRequiredProperty(MESSAGE_SUCCESS_CAR_CREATED));
            }});
            return response;
        }
    }

    @RequestMapping(value = "/getDrivers", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> getDrivers() {

        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> parametersOfDriver = null;

        List<Driver> drivers = carService.getDrivers();

        for (Driver driver : drivers) {
            parametersOfDriver = new HashMap<>();
            parametersOfDriver.put("id", driver.getId().toString());
            parametersOfDriver.put("first_name", driver.getFirstName());
            parametersOfDriver.put("last_name", driver.getLastName());

            result.add(parametersOfDriver);

            parametersOfDriver = null;
        }

        return result;
    }
}


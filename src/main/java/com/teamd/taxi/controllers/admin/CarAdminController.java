package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.models.admin.AdminResponseModel;
import com.teamd.taxi.models.admin.CarsPageModel;
import com.teamd.taxi.service.AdminPagesUtil;
import com.teamd.taxi.service.CarService;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Resource
    private Environment env;

    @Autowired
    private CarService carService;

    @Autowired
    private AdminPagesUtil pagesUtil;

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
    public Set<String> showAddFormCar() {
        Set<String> featureNames = new HashSet<>();

        List<Feature> features = new ArrayList<>();
        features = carService.getCarFeatures();
        for (Feature f : features) {
            featureNames.add(f.getName());
        }

        return featureNames;
    }

    @RequestMapping(value = "/create_car", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createNewCar(@RequestBody String jsonBody, Model model) {

        System.out.println(jsonBody);

        return new ResponseEntity<Object>(new String("+++"), HttpStatus.OK);
    }

}


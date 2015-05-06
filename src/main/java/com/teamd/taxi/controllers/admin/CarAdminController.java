package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.models.admin.CarsPageModel;
import com.teamd.taxi.service.AdminPagesUtil;
import com.teamd.taxi.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private CarService carService;

    @Autowired
    private AdminPagesUtil pagesUtil;

    //URL example: cars?page=1&order=model
    @RequestMapping(value = "/cars", method = RequestMethod.GET)
    public String viewCars(@Valid CarsPageModel pageModel, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            //TODO: Return 404 here or other error page
            return "404";
        }

        //System.out.println(pageModel);
        Sort sort = new Sort(new Sort.Order(DEFAULT_SORT_DIRECTION, pageModel.getOrder()));
        Page<Car> cars = carService.getCars(new PageRequest(pageModel.getPage(), DEFAULT_NUM_OF_RECORDS_ON_PAGE, sort));
        model.addAttribute("page", cars);

        ArrayList<Integer> pagination = pagesUtil.getPagination(pageModel.getPage(), cars.getTotalPages());
        model.addAttribute("pagination", pagination);


        model.addAttribute("carFeatures", carService.getCarFeatures());

        return "admin/cars";
    }

    @RequestMapping(value = "/getForm_add_car", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Set<String> showAddFormCar() {
        Set<String> featureNames = new HashSet<>();

        List<Feature> features = new ArrayList<>();
        features = carService.getCarFeatures();
        for(Feature f : features){
            featureNames.add(f.getName());
        }

        return featureNames;
    }

    @RequestMapping(value = "/create_car", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createNewCar(@RequestBody String jsonBody,Model model){

        System.out.println(jsonBody);

        return new ResponseEntity<Object>(new String("+++"), HttpStatus.OK);
    }

}

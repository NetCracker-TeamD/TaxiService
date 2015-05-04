package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created on 02-May-15.
 *
 * @author Nazar Dub
 */
@Controller
@RequestMapping("/admin")
public class CarAdminController {

    public static final int DEFAULT_NUM_OF_RECORDS_ON_PAGE = 20;

    @Autowired
    private CarService carService;

    //URL example: cars?page=1&order=byCarModel
    @RequestMapping(value = "/cars", method = RequestMethod.GET)
    public String viewCars(@RequestParam(value = "page", required = false, defaultValue = "0") String page,
                           @RequestParam(value = "order", required = false, defaultValue = "model") String order,
                           Model model) {


        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, CarOrder.MODEL.toString().toLowerCase()));


        Page<Car> cars = carService.getCars(new PageRequest(0, DEFAULT_NUM_OF_RECORDS_ON_PAGE, sort));
        model.addAttribute("page", cars);
        return "admin/cars";
    }

    private enum CarOrder {
        MODEL
    }

}

package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.controllers.admin.orders.CarOrder;
import com.teamd.taxi.entity.Car;
import com.teamd.taxi.models.admin.CarsPageModel;
import com.teamd.taxi.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

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

    //URL example: cars?page=1&order=model
    @RequestMapping(value = "/cars", method = RequestMethod.GET)
    public String viewCars(@Valid CarsPageModel pageModel, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            //TODO: Return 404 here
            return "404";
        }

        //System.out.println(pageModel);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, pageModel.getOrder()));

        Page<Car> cars = carService.getCars(new PageRequest(pageModel.getPage(), DEFAULT_NUM_OF_RECORDS_ON_PAGE, sort));
        model.addAttribute("page", cars);
        return "admin/cars";
    }

}

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;

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

}

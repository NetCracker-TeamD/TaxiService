package com.teamd.taxi.controllers.driver;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Іван on 02.05.2015.
 */
@Controller
@RequestMapping("/driver")
public class OrderController {

    @RequestMapping(value ="/order", method = RequestMethod.GET)
    public String viewQueue(Model model, HttpServletRequest request) {
        return "driver/drv-current-order";
    }

}

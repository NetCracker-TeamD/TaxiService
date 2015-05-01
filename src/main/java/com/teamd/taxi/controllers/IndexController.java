package com.teamd.taxi.controllers;


import com.teamd.taxi.entity.User;
import com.teamd.taxi.models.RegistrationForm;
import com.teamd.taxi.service.CustomerUserService;
import com.teamd.taxi.validation.RegistrationFormPasswordValidator;
import com.teamd.taxi.validation.UniqueEmailValidator;
import com.teamd.taxi.validation.UniqueEmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import javax.validation.Valid;
import java.util.List;


@Controller
public class IndexController {

    private static final Logger logger = Logger.getLogger(IndexController.class);

    @Autowired
    private UniqueEmailValidator uniqueEmailValidator;

    @Autowired
    private CustomerUserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(
                new RegistrationFormPasswordValidator(),
                uniqueEmailValidator
        );
    }

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        mav.addObject("registrationForm", new RegistrationForm());
        return mav;
    }

    @RequestMapping("/register")
    public String registerNewCustomer(
            @Valid RegistrationForm form, BindingResult errors) {
        if (errors.hasErrors()) {
            //report errors
        } else {
            User user = new User();
            user.setFirstName(form.getFirstName());
            user.setLastName(form.getLastName());
            user.setEmail(form.getEmail());
            user.setPhoneNumber(form.getPhoneNumber());
            user.setUserPassword(form.getPassword());

            userService.registerNewCustomerUser(user);
        }
        return "coderesolving";
    }
}

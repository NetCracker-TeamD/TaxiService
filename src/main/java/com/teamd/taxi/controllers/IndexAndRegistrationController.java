package com.teamd.taxi.controllers;


import com.teamd.taxi.entity.User;
import com.teamd.taxi.models.RegistrationForm;
import com.teamd.taxi.service.CustomerUserService;
import com.teamd.taxi.validation.RegistrationFormPasswordValidator;
import com.teamd.taxi.validation.UniqueEmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;


@Controller
public class IndexAndRegistrationController {

    private static final Logger logger = Logger.getLogger(IndexAndRegistrationController.class);

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
        logger.info("Auth status: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        mav.addObject("registrationForm", new RegistrationForm());
        return mav;
    }

    @RequestMapping("/register")
    public String registerNewCustomer(
            @Valid RegistrationForm form, BindingResult errors) {
        if (errors.hasErrors()) {
            List<ObjectError> errorList = errors.getAllErrors();
            logger.info("RegisterForm validation errors:");
            for (ObjectError objectError : errorList) {
                logger.info(objectError.toString());
            }
            //TODO: report errors
        } else {
            User user = new User();
            user.setFirstName(form.getFirstName());
            user.setLastName(form.getLastName());
            user.setEmail(form.getEmail());
            user.setPhoneNumber(form.getPhoneNumber());
            user.setUserPassword(form.getPassword());

            userService.registerNewCustomerUser(user);
        }
        //TODO: return something normal
        return "coderesolving";
    }

    @RequestMapping("/confirm/{confirmationCode}")
    public void confirmUser(@PathVariable("confirmationCode") String code,
    /*to prevent view resolving*/ HttpServletResponse response) {
        userService.confirmUser(code);
        //TODO: generate view and error handling
    }
}

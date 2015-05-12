package com.teamd.taxi.controllers;


import com.teamd.taxi.entity.User;
import com.teamd.taxi.exception.UserAlreadyConfirmedException;
import com.teamd.taxi.models.RegistrationForm;
import com.teamd.taxi.service.CustomerUserService;
import com.teamd.taxi.validation.RegistrationFormPasswordValidator;
import com.teamd.taxi.validation.UniqueEmailValidator;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        AbstractAuthenticationToken auth = (AbstractAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();
        logger.info("Auth status: " + auth.getPrincipal()
                + ", " + auth.getCredentials() + ", " + auth.getAuthorities() + ", " + auth.isAuthenticated());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        mav.addObject("registrationForm", new RegistrationForm());
        return mav;
    }

    @RequestMapping("/register")
    @ResponseBody
    public Map<String, Object> registerNewCustomer(
            @Valid RegistrationForm form, BindingResult errors) {
        Map<String, Object> retValue = new HashMap<>();
        if (errors.hasErrors()) {
            logger.info("RegisterForm validation errors");
            Map<String, List<String>> fieldErrors = new HashMap<>();
            List<FieldError> fieldErrorList = errors.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                String field = fieldError.getField();
                List<String> messages = fieldErrors.get(field);
                if (messages == null) {
                    messages = new ArrayList<>();
                    fieldErrors.put(field, messages);
                }
                messages.add(fieldError.getDefaultMessage());
            }
            retValue.put("fieldErrors", fieldErrors);
            retValue.put("success", false);
        } else {
            User user = new User();
            user.setFirstName(form.getFirstName());
            user.setLastName(form.getLastName());
            user.setEmail(form.getEmail());
            user.setPhoneNumber(form.getPhoneNumber());
            user.setUserPassword(form.getPassword());

            userService.registerNewCustomerUser(user);
            retValue.put("success", true);
        }
        return retValue;
    }

    @RequestMapping("/confirm/{confirmationCode}")
    public void confirmUser(@PathVariable("confirmationCode") String code,
    /*to prevent view resolving*/ HttpServletResponse response) throws UserAlreadyConfirmedException {
        userService.confirmUser(code);
        //TODO: generate view and error handling
    }
}

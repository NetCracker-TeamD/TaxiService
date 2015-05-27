package com.teamd.taxi.controllers;


import com.teamd.taxi.authentication.Utils;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.exception.UserAlreadyConfirmedException;
import com.teamd.taxi.models.MapResponse;
import com.teamd.taxi.models.RegistrationForm;
import com.teamd.taxi.service.CustomerUserService;
import com.teamd.taxi.service.email.MailService;
import com.teamd.taxi.validation.RegistrationFormPasswordValidator;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class IndexAndRegistrationController {

    private static final Logger logger = Logger.getLogger(IndexAndRegistrationController.class);

    @Resource
    private Environment env;

    @Autowired
    private CustomerUserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(
                new RegistrationFormPasswordValidator()
        );
    }

    @RequestMapping("/")
    public String redirect() {
        if (!Utils.isAuthenticated()) {
            return "redirect:order";
        }
        String redirectUrl = "/error";
        switch (Utils.getCurrentUserRole()) {
            case "ROLE_CUSTOMER":
                redirectUrl = "order";
                break;
            case "ROLE_ADMINISTRATOR":
                redirectUrl = "admin/statistic";
                break;
            case "ROLE_DRIVER":
                redirectUrl = "driver/queue";
                break;
        }
        return "redirect:" + redirectUrl;
    }

    @RequestMapping("/about")
    public String about(Model model) {
        return "user/about";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        if (Utils.isAuthenticated()) {
            return "redirect:/order";
        }
        return "user/login";
    }

    @RequestMapping("/register")
    public String register(Model model) {
        if (Utils.isAuthenticated()) {
            return "redirect:/order";
        }
        return "user/register";
    }

    @RequestMapping(value = "/checkFreeEmail", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        return "{\"isEmailFree\":" + userService.isEmailFree(email) + "}";
    }

    @RequestMapping(value = "/signup", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> registerNewCustomer(
            @Valid RegistrationForm form, BindingResult errors) throws MessagingException {
        Map<String, Object> retValue = new HashMap<>();
        if (errors.hasErrors()) {
            logger.info("RegisterForm validation errors");
            retValue.put("fieldErrors", Utils.convertToMap(env, errors));
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
    public String confirmUser(@PathVariable("confirmationCode") String code, Model model) {
        String message;
        try {
            boolean success = userService.confirmUser(code);
            message = success ? "Email confirmed successful. Now you can sign in." :
                    "Confirmation failed.";
        } catch (UserAlreadyConfirmedException ex) {
            message = "User already confirmed";
        }
        model.addAttribute("message", message);
        return "confirmation";
    }

    @RequestMapping(value = "/isUserLogged", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> checkLogin() {
        boolean isAuthenticated = Utils.isAuthenticated();
        MapResponse mapResponse = new MapResponse().put("isAuthenticated", isAuthenticated);
        if (isAuthenticated) {
            mapResponse.put("role", Utils.getCurrentUserRole());
            mapResponse.put("userId", Utils.getCurrentUser().getId());
        }
        return mapResponse;
    }
}

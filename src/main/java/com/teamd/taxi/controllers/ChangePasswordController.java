package com.teamd.taxi.controllers;

import com.teamd.taxi.authentication.AuthenticatedUser;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.service.CustomerUserService;
import com.teamd.taxi.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by Anton on 14.05.2015.
 */
@Controller
public class ChangePasswordController {
    @Autowired
    DriverService driverService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private CustomerUserService userService;

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String viewPageChangePassword(Model map, @RequestParam Map<String, String> requestParams) {
        String oldPass = requestParams.get("oldpass");
        String newpass = requestParams.get("newpass");
        String repass = requestParams.get("repass");
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        AuthenticatedUser auth = (AuthenticatedUser) authentication.getPrincipal();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DRIVER"))) {
            Driver driver = driverService.getDriver((int) auth.getId());
            if (newpass != null && repass != null /*&& newpass.length() > 5 */ && newpass.equals(repass)
                    && encoder.matches(oldPass, driver.getPassword())) {
                driver.setPassword(encoder.encode(newpass));
                driverService.save(driver);
                map.addAttribute("info", "Password successfully changed");
            } else {
                map.addAttribute("error", "Incorrect password");
            }
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            User user = userService.findById(auth.getId());
            if (newpass != null && repass != null /*&& newpass.length() > 5 */ && newpass.equals(repass)
                    && encoder.matches(oldPass, user.getUserPassword())) {
                user.setUserPassword(encoder.encode(newpass));
                userService.save(user);
                map.addAttribute("info", "Password successfully changed");
            } else {
                map.addAttribute("error", "Incorrect password");
            }
        }
        return "changePassword";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String viewPageChangePassword() {
        return "changePassword";
    }


}

package com.teamd.taxi.controllers.driver;

import com.teamd.taxi.authentication.AuthenticatedUser;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.service.DriverService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by Anton on 16.05.2015.
 */
@Controller
@RequestMapping("/driver")
public class ProfileDriverController {
    @Autowired
    private DriverService driverService;
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getDriverProfile(Model model, @RequestParam Map<String, String> requestParam){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        AuthenticatedUser auth = (AuthenticatedUser) authentication.getPrincipal();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DRIVER"))) {
            Driver driver=driverService.getDriver(((Long)auth.getId()).intValue());
            model.addAttribute("driver",driver);
            model.addAttribute("car",driver.getCar());
        }
        return "driver/drv-profile";
    }
}

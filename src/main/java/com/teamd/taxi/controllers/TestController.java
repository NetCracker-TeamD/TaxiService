package com.teamd.taxi.controllers;

import com.teamd.taxi.models.RegistrationForm;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Map;

/**
 * Created by anton on 5/12/15. 
 * For dev purposes, watch data recieved by server
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @RequestMapping(value = "/json/post", method = RequestMethod.POST)
    @ResponseBody
    public String post(@RequestBody String jsonString) {
        return jsonString;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public String get(@RequestParam Map<String,String> allRequestParams) {
        StringBuilder sb = new StringBuilder();
        sb.append("This is GET query\n");
        for (Map.Entry<String, String> entry : allRequestParams.entrySet()){
            sb.append("key : '");
            sb.append(entry.getKey());
            sb.append("' ; value = '");
            sb.append(entry.getValue());
            sb.append("' ;\n");
        }
        return sb.toString();
    }

    @RequestMapping(value = "/wait/{ms}")
    @ResponseBody
    public String wait(@PathVariable("ms") int milliseconds) {
        StringBuilder sb = new StringBuilder();
        sb.append("Script stars at ");
        sb.append((new Date()).toString());
        sb.append("\n");
        try {
            Thread.sleep(milliseconds);
            sb.append("Script waited for ");
            sb.append(milliseconds);
            sb.append("ms\n");
        } catch (InterruptedException e) {
            sb.append("Script was interrupted with message : '");
            sb.append(e.getMessage());
            sb.append("'\n");
        }
        sb.append("Script ends at ");
        sb.append((new Date()).toString());
        sb.append("\n");
        return sb.toString();
    }

}

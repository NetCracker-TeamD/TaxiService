package com.teamd.taxi.controllers;

import com.teamd.taxi.models.RegistrationForm;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by anton on 5/12/15.
 * For dev purposes, whatch data recieved by server
 */
@Controller
@RequestMapping("/mirror")
public class MirrorController {
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
}

package com.teamd.taxi.authentication;

import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Олег on 19.05.2015.
 */
public class Utils {
    public static AuthenticatedUser getCurrentUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication authentication = ctx.getAuthentication();
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return user;
    }

    public static Map<String, String> convertToMap(Environment env, BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : result.getAllErrors()) {
            String msg = env.getProperty(error.getCode());
            if (msg == null) {
                msg = error.getDefaultMessage();
            }
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                errors.put(fieldError.getField(), msg);
            } else {
                errors.put(error.getObjectName(), msg);
            }
        }
        return errors;
    }
}

package com.teamd.taxi.authentication;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Олег on 20.05.2015.
 */
public class SystemAccessDeniedHandler implements AccessDeniedHandler {

    private String errorPage;

    public SystemAccessDeniedHandler() {
    }

    public SystemAccessDeniedHandler(String errorPage) {
        this.errorPage = errorPage;
    }

    public String getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        System.out.println("on access denied");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
package com.teamd.taxi.authentication;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamd.taxi.authentication.driver.DriverUsernamePasswordAuthenticationToken;
import com.teamd.taxi.authentication.user.UserUsernamePasswordAuthenticationToken;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class SystemAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = Logger.getLogger(SystemAuthenticationProcessingFilter.class);

    private boolean postOnly = true;

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String username = this.obtainUsername(request);
            String password = this.obtainPassword(request);
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            username = username.trim();
            String radioAuthenticationType = request.getParameter("radioAuthenticationType");

            UsernamePasswordAuthenticationToken authRequest = null;
            if ("driver".equals(radioAuthenticationType)) {
                authRequest = new DriverUsernamePasswordAuthenticationToken(username, password);
            } else {
                authRequest = new UserUsernamePasswordAuthenticationToken(username, password);
            }
            this.setDetails(request, authRequest);

            logger.info("Authentication attempt");
            logger.info("Username = " + username + ", password = " + password);
            logger.info("radioAuthenticationType = " + radioAuthenticationType);
            logger.info("AuthRequestClass = " + authRequest.getClass());

            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        SavedRequestAwareAuthenticationSuccessHandler srh = new SavedRequestAwareAuthenticationSuccessHandler();
        this.setAuthenticationSuccessHandler(srh);
        srh.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public void sendRedirect(HttpServletRequest httpServletRequest,
                                     HttpServletResponse httpServletResponse, String s) throws IOException {
            }
        });
        super.successfulAuthentication(request, response, chain, authResult);
        Map<String, Object> responseObject = new HashMap<>();
        responseObject.put("authenticationStatus", true);
        sendJsonResponse(new HttpServletResponseWrapper(response), responseObject);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler("/");
        failureHandler.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public void sendRedirect(HttpServletRequest httpServletRequest,
                                     HttpServletResponse httpServletResponse, String s) throws IOException {
            }
        });
        this.setAuthenticationFailureHandler(failureHandler);
        super.unsuccessfulAuthentication(request, response, failed);

        Map<String, Object> responseObject = new HashMap<>();
        responseObject.put("authenticationStatus", false);
        sendJsonResponse(new HttpServletResponseWrapper(response), responseObject);
    }

    private void sendJsonResponse(HttpServletResponseWrapper response, Map<String, Object> responseObject) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        try (OutputStream stream = response.getOutputStream()) {
            new ObjectMapper().writeValue(stream, responseObject);
        }
    }
}
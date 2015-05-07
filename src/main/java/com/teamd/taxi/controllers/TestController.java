package com.teamd.taxi.controllers;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.models.RegistrationForm;
import com.teamd.taxi.persistence.repository.InfoRepository;
import com.teamd.taxi.persistence.repository.ReportsRepository;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import com.teamd.taxi.persistence.repository.UserRepository;

import com.teamd.taxi.validation.RegistrationFormPasswordValidator;
import org.apache.log4j.Logger;

import org.hibernate.Hibernate;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.query.Jpa21Utils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class TestController {

    private static final Logger logger = Logger.getLogger(TestController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportsRepository reportsRepository;

    @RequestMapping("/testData")
    public void testData(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, String[]> params = request.getParameterMap();
        for (String s : params.keySet()) {
            logger.info("Key: " + s + ", Value: " + Arrays.toString(params.get(s)));
        }
        response.getWriter().append("{status:'OK'}");
    }

    @RequestMapping("/history")
    public String historyMethod() throws UnsupportedEncodingException {
        UriComponentsBuilder builder = MvcUriComponentsBuilder
                .fromMethodName(this.getClass(), "historyMethod");
        logger.info(builder.build());
        logger.info(builder.queryParam("paramName", "значен ние").build().encode());
        return "history";
    }

    @RequestMapping("/test")
    public String test(@RequestParam("p") String param, Model model) {
        model.addAttribute("message", param);
        model.addAttribute("escapedMessage", HtmlUtils.htmlEscape(param));
        return "test";
    }

    //@InitBinder
    public void initBinder(WebDataBinder binder) {
        // The date format to parse or output your dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        // Create a new CustomDateEditor
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        // Register it as custom editor for the Date type
        binder.registerCustomEditor(Date.class, editor);
    }

    @RequestMapping(value = "/date", produces = "application/json;charset=UTF-8")
    public void dateTest(Date date, HttpServletResponse response) throws IOException {
        response.getWriter().append(date.getTime() + "");
    }

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private TaxiOrderRepository orderRepository;

    @RequestMapping(value = "/testInfo")
    public void testInfo(HttpServletResponse response, @RequestParam("page") int page) throws IOException {
        try (Writer writer = response.getWriter()) {
            List<TaxiOrder> ords = orderRepository.findBySomething(Arrays.asList(1, 5), new PageRequest(page, 20));
            for (TaxiOrder ord : ords) {
                writer.append("" + ord.getId()).append("\n");
            }
        }
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class
    })
    public void handleValidation(Exception e, HttpServletResponse response) {
        try {
            response.getWriter().append(e.getClass().getName());
        } catch (IOException ex) {

        }
    }
}

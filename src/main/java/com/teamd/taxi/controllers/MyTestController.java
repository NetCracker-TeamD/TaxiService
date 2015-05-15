package com.teamd.taxi.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.teamd.taxi.authentication.AuthenticatedUser;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.models.RegistrationForm;
import com.teamd.taxi.persistence.repository.*;

import com.teamd.taxi.validation.RegistrationFormPasswordValidator;
import org.apache.log4j.Logger;

import org.hibernate.Hibernate;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.Jpa21Utils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class MyTestController {

    private static final Logger logger = Logger.getLogger(MyTestController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaxiOrderRepository orderRepository;

    @Autowired
    private ReportsRepository reportsRepository;

    @Autowired
    private UserAddressRepository addressRepository;

    @RequestMapping("/test")
    @ResponseBody
    public String test(HttpServletRequest request) throws IOException, ServletException {
        User user = new User(null, "first", "last", UserRole.ROLE_ANONYMOUS, "+123");
        List<UserAddress> addrs = new ArrayList<>();
        addrs.add(new UserAddress(null, "name1", "addr1"));
        addrs.add(new UserAddress(null, "name2", "addr2"));
        addrs.add(new UserAddress(null, "name3", "addr3"));
        user = userRepository.save(user);

        for (UserAddress addr : addrs) {
            addr.setUser(user);
        }
        addressRepository.save(addrs);

        return "success";
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

    @Autowired
    private UserAddressRepository repository;

    @RequestMapping("/authTest/{groupId}")
    @Transactional
    public void testAuth(@PathVariable("groupId") UserGroup group, HttpServletResponse response) {
        if (group == null) {
            System.out.println("group not exist");
            return;
        }

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            AuthenticatedUser auth = (AuthenticatedUser) authentication.getPrincipal();
            User user = userRepository.findOne(auth.getId());
            List<GroupList> groupLists = user.getGroups();
            boolean found = false;
            boolean isManager = false;
            for (GroupList groupList : groupLists) {
                if (groupList.getUserGroup().equals(group)) {
                    found = true;
                    isManager = groupList.isManager();
                    break;
                }
            }
            System.out.println("found = " + found);
            System.out.println("isManager = " + isManager);
            if (!found) {
                //...
            }
            if (!isManager) {
                //...
            }

            System.out.println(authentication.getClass());
            System.out.println(authentication.getPrincipal());
            System.out.println(authentication.getAuthorities()
                    .contains(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
            System.out.println(authentication.getCredentials());
            System.out.println(authentication.getDetails());
        }
        response.setStatus(200);
    }
}

package com.teamd.taxi.controllers.user;

import com.teamd.taxi.entity.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Anton on 29.04.2015.
 */
@Controller
@RequestMapping("/user")
public class HistoryUserController {

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String viewHistory(Model model, HttpServletRequest request) {
        //request.getParameter("sort")
        //request.getParameter("page")
        int numberOfRows = 5;
        List<TaxiOrder> routeList;
        routeList = getListTaxiOrder(1, "id");
        model.addAttribute("orderList", routeList);
        model.addAttribute("pages", 2);
        return "user-history";
    }

    @SuppressWarnings("deprecation")
    List<TaxiOrder> getListTaxiOrder(int pageNumber, String filter) {
        Route route;
        List<TaxiOrder> orderList = new ArrayList<TaxiOrder>();
        TaxiOrder taxiOrder;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        for (int i = 1; i <= 7; i++) {
            taxiOrder = new TaxiOrder();
            taxiOrder.setId((long) 1234 + i);
            //init taxiorder
            User user = new User();
            user.setFirstName("Anton" + i);
            user.setPhoneNumber("063538702" + i);
            taxiOrder.setComment("Nice");
            taxiOrder.setCustomer(user);
            PaymentType paymentType = PaymentType.CARD;
            taxiOrder.setPaymentType(paymentType);
            taxiOrder.setExecutionDate(Calendar.getInstance(Locale.ENGLISH));
            List<Feature> features = new ArrayList<Feature>();
            Feature feature = new Feature();
            feature.setId(1);
            feature.setName("WiFi");
            features.add(feature);
            feature = new Feature();
            feature.setId(2);
            feature.setName("Animal transportation");
            features.add(feature);
            feature = new Feature();
            feature.setId(3);
            feature.setName("Smoking driver");
            features.add(feature);
            feature = new Feature();
            feature.setId(4);
            feature.setName("Air-conditioner");
            features.add(feature);
            taxiOrder.setFeatures(features);

            //route
            ArrayList<Route> routeList = new ArrayList<Route>();
            route = new Route();
            route.setId((long) i);
            route.setDestinationAddress("бул. Лесі Українки, 14, Київ, Украина");
            route.setSourceAddress("вул. Круглоуніверситетська, 9 Київ, Украина");
            route.setDistance(1.8F);

            Calendar startTime = Calendar.getInstance(Locale.ENGLISH);
            route.setStartTime(startTime);

            Calendar completionTime = ((Calendar)startTime.clone());
            completionTime.add(Calendar.HOUR, 5);
            route.setCompletionTime(completionTime);

            route.setStatus(RouteStatus.COMPLETED);
            route.setOrder(taxiOrder);
            routeList.add(route);

            route = new Route();
            route.setDestinationAddress("вул. Ванди Василевської, 1/28, Київ, Украина");
            route.setSourceAddress("бул. Лесі Українки, 14, Київ, Украина");
            route.setDistance(5.6F);
            route.setStartTime(Calendar.getInstance(Locale.ENGLISH));
            route.setCompletionTime(Calendar.getInstance(Locale.ENGLISH));
            route.setStatus(RouteStatus.COMPLETED);
            routeList.add(route);
            taxiOrder.setRoutes(routeList);
            orderList.add(taxiOrder);
        }
        return orderList;
    }
}

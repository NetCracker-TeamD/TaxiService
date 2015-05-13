package com.teamd.taxi.controllers.driver;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import com.teamd.taxi.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by Іван on 02.05.2015.
 */
@Controller
@RequestMapping("/driver")
public class CurrentOrderController {
    private static final Logger log = Logger.getLogger(CurrentOrderController.class);

    private String status;

    private Driver driver;
    @Autowired
    private RouteService routeService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CarService carService;

    @Autowired
    private TaxiOrderService taxiOrderServiceImpl;

    private TaxiOrder taxiOrder;

    private String source, dest;
    private long id;

    //hardcode) must take from session
    private int driverId = 6;

    private int routeId;

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    private String viewQueue(Model model, HttpServletRequest requst) {



        return "driver/drv-current-order";
    }

    @RequestMapping(value = "/assign", method = RequestMethod.GET)
    private String assignOrder(Model model, HttpServletRequest requst) {

        //hardcode
        driver = driverService.getDriver(driverId);

        if(!checkIdParam(requst)){
//            model.addAttribute("errorMessage", "No such free request");
            return "redirect:error";
        }

        return "driver/drv-current-order";
    }


    @RequestMapping(value = "/error", method = RequestMethod.GET)
    private String error(Model model, HttpServletRequest requst) {

        //hardcode
        driver = driverService.getDriver(driverId);

        model.addAttribute("errorMessage", "No such free request or wrong URL address");

        return "driver/driver-error-page";
    }


    private boolean checkIdParam(HttpServletRequest requst) {
        if (requst.getParameter("id") != null) {
            id = Long.valueOf(requst.getParameter("id"));
        } else
            return false;

        if ((taxiOrder = taxiOrderServiceImpl.findOneById(id)) == null) {

            return false;
        }
        log.info("isDestinationLocationsChain ="+taxiOrder.getServiceType().isDestinationLocationsChain());
        if (!checkDriverAndOrderFeature(driver.getId(), taxiOrder.getId())) {
            return false;
        }

        List<Route> routes = routeService.getFreeRouts(id);
//        taxiOrderService.findOneById(id).getRoutes();
        if (!routes.isEmpty()) {
            if (!taxiOrder.getServiceType().isDestinationLocationsChain()) {
                if (requst.getParameter("sourse") != null){
                    source = requst.getParameter("sourse");
                    if ((dest = requst.getParameter("dest")) != null) {
                        for ( Route r : routes)
                            if(r.getSourceAddress().equals(source) && r.getDestinationAddress().equals(dest)) {
                                initAssignStatus(driver, r);
                                return true;
                            }
                    }else{
                        for ( Route r : routes)
                            if(r.getSourceAddress().equals(source)) {
                                initAssignStatus(driver, r);
                                return true;
                            }
                    }
                } else {
                    return false;
                }
            } else {
                Route[] massRoute = new Route[routes.size()];
                initAssignStatus(driver, routes.toArray(massRoute));
                return true;
            }
        }else{
            return false;
        }
        return false;
    }



    private void initAssignStatus( Driver driver, Route ...r ){

        for (int i = 0; i < r.length; i++ ){
            r[i].setStatus(RouteStatus.ASSIGNED);
        }
        List<Route> rlist = new ArrayList<>(driver.getRoutes());
        rlist.addAll(Arrays.asList(r));
        driver.setRoutes(rlist);
    }
    // check for responsibility driver and order features
    private boolean checkDriverAndOrderFeature(int driverId, long orderId){
        List<Feature> drFeatures = driverService.getDriver(driverId).getFeatures();
        drFeatures.addAll(carService.getFeatureCarByDriverID(driverId));
        List<Feature> toFeature = taxiOrderServiceImpl.findOneById(orderId).getFeatures();
        if(drFeatures.containsAll(toFeature))
            return true;
        else return false;
    }

}

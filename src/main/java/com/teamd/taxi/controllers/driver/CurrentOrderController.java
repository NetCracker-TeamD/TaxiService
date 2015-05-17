package com.teamd.taxi.controllers.driver;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.models.AssembledOrder;
import com.teamd.taxi.models.AssembledRoute;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import com.teamd.taxi.service.DriverService;
import com.teamd.taxi.service.RouteService;
import com.teamd.taxi.service.TaxiOrderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Іван on 02.05.2015.
 */
@Controller
@RequestMapping("/driver")
public class CurrentOrderController {
    private static final Logger log = Logger.getLogger(CurrentOrderController.class);

    @Autowired
    private RouteService routeService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TaxiOrderService taxiOrderService;

    @Autowired
    private TaxiOrderRepository taxiOrderRepository;

    //hardcode) must take from session
//        TO DO
    private int driverId = 6;

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    private String viewQueue(Model model, HttpServletRequest requst) {
        boolean blockNewRouteBtn = false,
                isActiveOrder = true;


        Driver driver = driverService.getDriver(driverId);
        int drvId = driver.getId();
        TaxiOrder taxiOrder;
        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(drvId)) == null) {
            System.out.println(" Driver have not any active order!!!!!! ");
            blockNewRouteBtn = true;
            isActiveOrder = false;

            model.addAttribute("isActiveOrder", isActiveOrder);
            model.addAttribute("newRouteBtn", blockNewRouteBtn);
        } else {
            log.info("DRIVER HAVE ORDER = " + taxiOrder);
            List<Route> routes = routeService.getRoutsByOrderAndDriverId(taxiOrder.getId(), drvId);
            log.info("ROUTES = " + routes);
            List<Route> sortRoutes = getSortRoutesForChain(taxiOrder, routes);
            isActiveOrder = true;
            for (Route route : sortRoutes) {
                System.out.println("Route: source = " + route.getSourceAddress() + "  dest = " + route.getDestinationAddress());
            }


            if (!isRouteChain(taxiOrder)) {
                blockNewRouteBtn = true;
            } else blockNewRouteBtn = false;

            model.addAttribute("isActiveOrder", isActiveOrder);
            model.addAttribute("sortRoutes", sortRoutes);
            model.addAttribute("blockNewRouteBtn", blockNewRouteBtn);
        }
        return "driver/drv-current-order";
    }

    private List<Route> getSortRoutesForChain(TaxiOrder taxiOrder, List<Route> routes) {
        AssembledOrder assembledOrder = AssembledOrder.assembleOrder(taxiOrder);
        List<AssembledRoute> assembledRoutes = assembledOrder.getAssembledRoutes();
        List<Route> sortedRoute = new ArrayList<>(routes.size());
        for (AssembledRoute aroute : assembledRoutes) {
            for (Route route : routes) {
                if (route.getSourceAddress().equals(aroute.getSource()) &&
                        route.getDestinationAddress().equals(aroute.getDestination())) {
                    sortedRoute.add(route);
                }
            }
        }
        return sortedRoute;
    }

    @RequestMapping(value = "/assign", method = RequestMethod.GET)
    private String assignOrder(Model model, HttpServletRequest requst) {

        if (!checkInputParam(requst)) {
            return "redirect:error";
        } else
            return "redirect:order";
    }

    @RequestMapping(value = "/lifeCircleOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String processOrder(@RequestParam(value = "status") String status) {

        Driver driver = driverService.getDriver(driverId);
        int drvId = driver.getId();
        TaxiOrder taxiOrder;
        JsonObject to = new JsonObject();

        System.out.println("finishOrder finishOrder finishOrder");
        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(drvId)) != null) {
            System.out.println("status = " + status);
            List<Route> routes = getSortRoutesForChain(taxiOrder, routeService.getRoutsByOrderAndDriverId(taxiOrder.getId(), drvId));
            if (status.equals("refuse")) {/// wtite for refuse
                System.out.println("-------------------------------------refuse");
                for (Route r : routes) {
                    if (r.getStatus() == RouteStatus.ASSIGNED || r.getStatus() == RouteStatus.IN_PROGRESS) {
                        System.out.println("REFUSED = " + r.getStatus() + " Source ADDRESS : " + r.getSourceAddress());
                        initRefuseStatus(r);
                    }
                }
            } else if (status.equals("complete")) {/// wtite for complete
                System.out.println("-------------------------------------complete");
                for (Route r : routes) {
                    if ((r.getStatus() == RouteStatus.IN_PROGRESS)) {
                        System.out.println("COMPLETED : " + r.getStatus() + " Source ADDRESS : " + r.getSourceAddress());
                        initCompleteStatus(r);
                        to.addProperty("routeStatus", r.getStatus().name());
                        to.addProperty("id", r.getId());
                        break;
                    }
                }
            } else if (status.equals("inProgress")) {
                System.out.println("-------------------------------------inProgress");
                for (Route r : routes) {
                    if ((r.getStatus() == RouteStatus.ASSIGNED)) {
                        System.out.println("INPROGRESS : " + r.getStatus() + " Source ADDRESS : " + r.getSourceAddress());
                        initCInProgressStatus(r);
                        to.addProperty("routeStatus", r.getStatus().name());
                        to.addProperty("id", r.getId());
                        break;
                    }
                }
            }
            to.addProperty("status", "stop");
            for (Route r : routes) {
                if (r.getStatus() == RouteStatus.ASSIGNED || r.getStatus() == RouteStatus.IN_PROGRESS) {
                    to.addProperty("status", "ok");
                    break;
                }
            }
        }
        return new Gson().toJson(to);
    }

    @RequestMapping(value = "/loadAddress")
    public
    @ResponseBody
    String[] loadAddress() {

        Driver driver = driverService.getDriver(driverId);
        TaxiOrder taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId());
        List<Route> routes = routeService.getRoutsByOrderAndDriverId(taxiOrder.getId(), driver.getId());
        String[] addresses = new String[routes.size() + 1];
        addresses[0] = routes.get(0).getSourceAddress();
        for (int i = 1; i < addresses.length; i++) {
            addresses[i] = routes.get(i - 1).getDestinationAddress();
        }
        return addresses;
    }

    @RequestMapping(value = "/setNewRoute", produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String driverCurrentOrder(@RequestParam(value = "source") String source,
                              @RequestParam(value = "destination") String dest) {
        JsonObject to = new JsonObject();

        Driver driver = driverService.getDriver(driverId);
        TaxiOrder taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId());
        Route route = new Route();
        route.setStatus(RouteStatus.ASSIGNED);
        route.setSourceAddress(source);
        route.setDestinationAddress(dest);
        route.setCustomerLate(false);
        route.setDriver(driver);
        route.setOrder(taxiOrder);
        // count distanse
        // route.setDistance();
        routeService.saveRoute(route);

        taxiOrder.getRoutes().add(route);
        taxiOrderRepository.save(taxiOrder);

        driver.getRoutes().add(route);
        driverService.save(driver);
        routeService.saveRoute(route);

        to.addProperty("source", source);
        to.addProperty("destination", dest);
        to.addProperty("status", "ok");
        to.addProperty("routeStatus", route.getStatus().name());
        to.addProperty("id", route.getId());
        return new Gson().toJson(to);
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    private String error(Model model) {
        model.addAttribute("errorMessage", "No such free request or wrong URL address");
        return "driver/driver-error-page";
    }


    private boolean checkInputParam(HttpServletRequest requst) {
        System.out.println(requst.getParameter("source") + "    " + requst.getParameter("dest"));
        Driver driver = driverService.getDriver(driverId);
        TaxiOrder taxiOrder;
        if ((taxiOrder = isOrderExist(requst)) == null) {
            return false;
        }
        //id пееревірено в isOrderExist(requst)
        long id = Long.valueOf(requst.getParameter("id"));

        if (!checkDriverAndOrderFeature(driver.getId(), taxiOrder.getId())) {
            return false;
        }
        Route route;
        // потрібно перевірити для ланцюжка і кількох машин!!!!
        List<Route> routes = routeService.getFreeRoutsByOrderID(id);
        // поки водій вибирав, всі роути ордера зайняли
        if (!routes.isEmpty()) {
            System.out.println("Routes not Empty");
            // якщо роут не ланцюжком
            if (!isRouteChain(taxiOrder)) {
                System.out.println(" Route Not Chain");
                // перевірка адресів призначення і прибуття
                if ((route = checkDestSourceParam(routes, requst)) != null) {
                    log.info("AssignStatus NOT Chain " + route);
                    routes.add(route);
                    initAssignStatus(driver, routes);
                    return true;
                } else {
                    log.info("Check Dest Source Param = " + route);
                    return false;
                }
            } else {
                System.out.println(" Route Chain");
                //якщо ланцюжком
                log.info("AssignStatus Chain " + routes);
                initAssignStatus(driver, routes);
                return true;
            }
        } else {
            return false;
        }
    }


    private boolean isRouteChain(TaxiOrder taxiOrder) {
        //only TaxiAsap and Taxi In Advance can be chain
        if (taxiOrder.getServiceType().getId() == 1 || taxiOrder.getServiceType().getId() == 2) {
            return taxiOrder.getServiceType().isDestinationLocationsChain();
        }
        return false;
    }

    // перевірка призначення і прибуття, тільки для роутів != chain
    private Route checkDestSourceParam(List<Route> routes, HttpServletRequest requst) {
        String source, dest;
        if (requst.getParameter("source") != null) {
            source = requst.getParameter("source");
            System.out.println("SOURCE");
            if ((dest = requst.getParameter("dest")) != null) {
                System.out.println("DESTINATION");
                for (Route r : routes) {
                    System.out.println("source = " + r.getSourceAddress() + "  address = " + r.getDestinationAddress());
                    if (r.getSourceAddress().equals(source) && r.getDestinationAddress().equals(dest)) {
                        return r;
                    }
                }
            } else {
                for (Route r : routes)
                    if (r.getSourceAddress().equals(source)) {
                        return r;
                    }
            }
        }
        return null;
    }

    private TaxiOrder isOrderExist(HttpServletRequest requst) {
        TaxiOrder taxiOrder;
        long id = -1;
        if (requst.getParameter("id") != null) {
            id = Long.valueOf(requst.getParameter("id"));
        } else {
            return null;
        }
        if ((taxiOrder = taxiOrderService.findOneById(id)) != null) {
            return taxiOrder;
        } else return null;

    }


    private void initAssignStatus(Driver driver, List<Route> routes) {
        for (int i = 0; i < routes.size(); i++) {
            routes.get(i).setStatus(RouteStatus.ASSIGNED);
            routes.get(i).setDriver(driver);
            routeService.saveRoute(routes.get(i));
        }
        List<Route> rlist = new ArrayList<>(driver.getRoutes());
        rlist.addAll(routes);
        driver.setRoutes(rlist);
        driverService.save(driver);
    }

    private void initCInProgressStatus(Route r) {
        Calendar calendar = Calendar.getInstance();
        r.setStatus(RouteStatus.IN_PROGRESS);
        r.setStartTime(calendar);
        routeService.saveRoute(r);
    }

    private void initCompleteStatus(Route r) {
        Calendar calendar = Calendar.getInstance();
        r.setStatus(RouteStatus.COMPLETED);
        r.setCompletionTime(calendar);
        routeService.saveRoute(r);
    }

    private void initRefuseStatus(Route r) {
        Calendar calendar = Calendar.getInstance();
        r.setStatus(RouteStatus.REFUSED);
        r.setCompletionTime(calendar);
        routeService.saveRoute(r);
    }

    // check for responsibility driver and order features
    private boolean checkDriverAndOrderFeature(int driverId, long orderId) {
        Driver driver = driverService.getDriver(driverId);
        List<Feature> drFeatures = driverService.getDriver(driverId).getFeatures();
        drFeatures.addAll(driver.getCar().getFeatures());
        List<Feature> toFeature = taxiOrderService.findOneById(orderId).getFeatures();
        if (drFeatures.containsAll(toFeature))
            return true;
        else return false;
    }

}

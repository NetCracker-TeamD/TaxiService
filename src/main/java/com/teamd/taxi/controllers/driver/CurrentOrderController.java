package com.teamd.taxi.controllers.driver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.InfoNotFoundException;
import com.teamd.taxi.exception.ItemNotFoundException;
import com.teamd.taxi.models.AssembledOrder;
import com.teamd.taxi.models.AssembledRoute;
import com.teamd.taxi.persistence.repository.RouteRepository;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import com.teamd.taxi.service.*;
import com.teamd.taxi.service.email.MailService;
import com.teamd.taxi.service.email.Notification;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Іван on 02.05.2015.
 */
@Controller
@RequestMapping("/driver")
public class CurrentOrderController {
    private static final Logger log = Logger.getLogger(CurrentOrderController.class);

    @Autowired
    private MapService mapService;

    @Autowired
    private InfoService infoService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private PriceCountService priceCountService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TaxiOrderService taxiOrderService;

    @Autowired
    private TaxiOrderRepository taxiOrderRepository;

    //hardcode) must take from session
//      TODO
    private int driverId = 6;

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    private String viewQueue(Model model, HttpServletRequest requst) {
        boolean isActiveOrder = true;
        Driver driver = driverService.getDriver(driverId);
        int drvId = driver.getId();
        TaxiOrder taxiOrder;
        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(drvId)) == null) {
            isActiveOrder = false;
            model.addAttribute("isActiveOrder", isActiveOrder);
        } else {
            List<Route> sortRoutes = getChainForDriver(taxiOrder, drvId);
            model.addAttribute("isActiveOrder", isActiveOrder);
            model.addAttribute("sortRoutes", sortRoutes);
        }
        return "driver/drv-current-order";
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
    public String processOrder(@RequestParam(value = "status") String status) throws InfoNotFoundException, ItemNotFoundException {

        Driver driver = driverService.getDriver(driverId);
        int drvId = driver.getId();
        TaxiOrder taxiOrder;
        JsonObject to = new JsonObject();

        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(drvId)) != null) {
            List<Route> routes = getChainForDriver(taxiOrder, drvId);
            long taxiOrderId = taxiOrder.getId();
            if (status.equals("refuse")) {/// wtite for refuse
                for (Route r : routes) {
                    if (r.getStatus() == RouteStatus.ASSIGNED) {
                        to.addProperty("status", "REFUSED");
                        initRefuseStatus(r);
                    }
                }
                to.addProperty("orderStatus", "refused");
            } else if (status.equals("complete")) {/// wtite for complete
                for (Route r : routes) {
                    if ((r.getStatus() == RouteStatus.IN_PROGRESS)) {
                        initCompleteStatus(r);
                        to.addProperty("status", "COMPLETED");
                        to.addProperty("id", r.getId());
                        break;
                    }
                }
                //перевіряєм чи ланцюжок замовлення виконаниний повністю, всі повиггі бути COMPLETED
                to.addProperty("orderStatus", "complete");
                boolean allComplete = true;
                for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
                    // якщо в водій вже виконував роути з замовлення і вони були відхилені, тоді видаляєм списку з перевірки
                    Route route = it.next();
                    if (route.getStatus() == RouteStatus.REFUSED) {
                        it.remove();
                    } else if (route.getStatus() != RouteStatus.COMPLETED) {
                        allComplete = false;
                        to.addProperty("orderStatus", "continue");
                        break;
                    }
                }
                if (allComplete) {
                    float totalPrice = 0;
                    List<Float> listPrice;
                    TaxiOrder taxiOrderBuf = taxiOrderService.findOneById(taxiOrderId);
                    List<Route> routesWithPrice = getChainForDriver(taxiOrderBuf, drvId);
                    if (taxiOrderBuf.getServiceType().isDestinationLocationsChain() != null &&
                            taxiOrderBuf.getServiceType().isDestinationLocationsChain()) {
                        listPrice = priceCountService.countPriceForLastChainOrder(taxiOrderBuf.getId(), driver.getId());
                        for (int i = routesWithPrice.size() - 1; i > 0; i--) {
                            Route r = routesWithPrice.get(i);
                            if (r.getStatus() == RouteStatus.COMPLETED) {
                                totalPrice += listPrice.get(r.getChainPosition() - 1);
                                r.setTotalPrice(listPrice.get(r.getChainPosition() - 1));
                                routeService.saveRoute(r);
                                if (r.getChainPosition() == 1)
                                    break;
                            }
                        }
                    } else {
                        long routeId = routesWithPrice.get(routesWithPrice.size() - 1).getId();
                        Route r = routeService.getRouteById(routeId);
                        totalPrice = priceCountService.countPriceForSingleRouteOrder(routeId);
                        r.setTotalPrice(totalPrice);
                        routeService.saveRoute(r);
                    }
                    to.addProperty("totalPrice", totalPrice);
                }

            } else if (status.equals("inProgress")) {
                for (Route r : routes) {
                    if ((r.getStatus() == RouteStatus.ASSIGNED)) {
                        initInProgressStatus(r);
                        to.addProperty("status", "IN PROGRESS");
                        to.addProperty("id", r.getId());
                        to.addProperty("orderStatus", "continue");
                        break;
                    }
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
        List<Route> routes = getChainForDriver(taxiOrder, driver.getId());
        for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
            Route route = it.next();
            if ((route.getStatus() == RouteStatus.COMPLETED) || (route.getStatus() == RouteStatus.REFUSED)) {
                it.remove();
            }
        }
        String[] addresses = new String[routes.size() + 1];
        addresses[0] = routes.get(0).getSourceAddress();
        for (int i = 0; i < routes.size(); i++) {
            addresses[i + 1] = routes.get(i).getDestinationAddress();
        }
        return addresses;
    }

    @RequestMapping(value = "/setNewRoute", produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String driverCurrentOrder(@RequestParam(value = "source") String source,
                              @RequestParam(value = "destination") String dest) throws Exception {
        JsonObject to = new JsonObject();
        Float distance;
        if ((distance = mapService.calculateDistanceInKilometers(source, dest)) != null) {
            Driver driver = driverService.getDriver(driverId);
            TaxiOrder taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId());
            AssembledOrder assembledOrder = AssembledOrder.assembleOrder(taxiOrder);
            int newRouteNumber = assembledOrder.getAssembledRoutes().size() + 1;
            //taxiOrder == null ???????
            //taxiOrder.serviceType ??????
            //carsAmount ??????
            Route route = new Route();
            route.setStatus(RouteStatus.ASSIGNED);
            route.setSourceAddress(source);
            route.setDestinationAddress(dest);
            route.setCustomerLate(false);
            route.setDriver(driver);
            route.setOrder(taxiOrder);
            route.setDistance(distance);
            route.setChainPosition(newRouteNumber);

            taxiOrder.getRoutes().add(route);
            driver.getRoutes().add(route);
            routeService.saveRoute(route);
//            taxiOrderRepository.save(taxiOrder);
            //driverService.save(driver);

            to.addProperty("source", source);
            to.addProperty("destination", dest);
            to.addProperty("status", "ok");
            to.addProperty("routeStatus", route.getStatus().name());
            to.addProperty("id", route.getId());
        } else {
            to.addProperty("status", "fail");
        }
        return new Gson().toJson(to);
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    private String error(Model model) {
        model.addAttribute("errorMessage", "No such free request or wrong URL address");
        return "driver/driver-error-page";
    }

    @RequestMapping(value = "/loadCurrentState", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String loadExecuteDate() {

        JsonObject to = new JsonObject();
        Driver driver = driverService.getDriver(driverId);
        TaxiOrder taxiOrder;

        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId())) != null) {
            long idleFreeTime = Long.valueOf(infoService.getIdleFreeTime("idle_free_time").getValue()) * 2000;
            long executeOrderDate = taxiOrder.getExecutionDate().getTimeInMillis();
            //TODO В БАЗІ ПОМИЛКА isDestinationLocationsChain повинно бути true/false aле не null
            if (taxiOrder.getServiceType().isDestinationLocationsChain() != null && taxiOrder.getServiceType().isDestinationLocationsChain()) {
                taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId());
                AssembledOrder assembledOrder = AssembledOrder.assembleOrder(taxiOrder);
                List<AssembledRoute> assRoutes = assembledOrder.getAssembledRoutes();
                System.out.println("AssRoutes.get(0).getRoutes().size()  " + assRoutes.get(0).getRoutes().size());
                if (assRoutes.get(0).getRoutes().size() == 1)
                    to.addProperty("newAddress", "enable");
                if (isChainOrderBegin(assRoutes, driver.getId())) {
                    to.addProperty("currentOrderState", "driverGoesToClient");
                } else {
                    Calendar completionDate;
                    taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId());
                    if ((completionDate = getTimeOfLastComletionRouteInChain(taxiOrder, driver.getId())) == null) {
                        to.addProperty("currentOrderState", "driverInProgress");
                    } else {
                        to.addProperty("lastCompletionRoute", completionDate.getTimeInMillis());
                        to.addProperty("currentOrderState", "driverWaytForClient");
                    }
                }
                to.addProperty("breakTime", idleFreeTime);

            } else {
                to.addProperty("currentOrderState", "driverInProgress");
                for (Route r : taxiOrder.getRoutes()) {
                    if ((r.getDriver().getId() == driver.getId()) && (r.getStatus() == RouteStatus.ASSIGNED)) {
                        to.addProperty("currentOrderState", "driverGoesToClient");
                    }
                }
            }
            to.addProperty("idleFreeTime", idleFreeTime);
            to.addProperty("executeOrderDate", executeOrderDate);
        } else {
            to.addProperty("currentOrderState", "noCurrentOrder");
        }

        return new Gson().toJson(to);
    }

    private boolean isChainOrderBegin(List<AssembledRoute> assRoutes, int driverId) {

        for (Route r : assRoutes.get(0).getRoutes()) {
            if (driverId == r.getDriver().getId() && r.getStatus() == RouteStatus.ASSIGNED) {
                return true;
            }
        }
        return false;
    }

    private Calendar getTimeOfLastComletionRouteInChain(TaxiOrder taxiOrder, int driverId) {
        List<Route> sortRoutes = getChainForDriver(taxiOrder, driverId);
        for (int i = 0; i < sortRoutes.size(); i++) {
            Route r = sortRoutes.get(i);
            if (driverId == r.getDriver().getId() && r.getStatus() == RouteStatus.IN_PROGRESS) {
                return null;
            } else if (driverId == r.getDriver().getId() && r.getStatus() == RouteStatus.ASSIGNED) {
                return sortRoutes.get(i - 1).getCompletionTime();
            }
        }
        return null;
    }

    private boolean checkInputParam(HttpServletRequest requst) {
        System.out.println(requst.getParameter("source") + "    " + requst.getParameter("dest"));
        Driver driver = driverService.getDriver(driverId);
        TaxiOrder taxiOrder;
        if ((taxiOrder = isOrderExist(requst)) == null) {
            return false;
        }
        if (taxiOrderService.findCurrentOrderByDriverId(driver.getId()) != null) {
            return false;
        }
        if (!checkDriverAndOrderFeature(driver.getId(), taxiOrder.getId())) {
            return false;
        }


        Route route;
        // потрібно перевірити для ланцюжка і кількох машин!!!!
        List<Route> routes = getChain(taxiOrder);

        // поки водій вибирав, всі роути ордера зайняли
        if (!routes.isEmpty()) {
            // якщо роут не ланцюжком
            if (!isRouteChain(taxiOrder)) {
                // перевірка адресів призначення і прибуття
                if ((route = checkDestSourceParam(routes, requst)) != null) {
                    log.info("AssignStatus NOT Chain " + route);
                    routes.add(route);
                    initAssignStatus(driver, routes);
                    return true;
                } else {
//                    log.info("Check Dest Source Param = " + route);
                    return false;
                }
            } else {
                System.out.println(" Route Chain");
                initAssignStatus(driver, routes);
                return true;
            }
        } else {
            return false;
        }
    }

    private List<Route> getChain(TaxiOrder taxiOrder) {
        AssembledOrder assembledOrder = AssembledOrder.assembleOrder(taxiOrder);
        List<AssembledRoute> assRoutes = assembledOrder.getAssembledRoutes();
        List<Route> routes = new ArrayList<>(assRoutes.size());

        for (int j = 0; j < assRoutes.size(); j++) {
            boolean bussy = false;
            for (Route r : assRoutes.get(j).getRoutes()) {
                if (r.getStatus() == RouteStatus.QUEUED) {
                    routes.add(r);
                    bussy = true;
                    break;
                }
            }
            if (bussy == false) {
                return new ArrayList<>();
            }
        }
        return routes;
    }

    private List<Route> getChainForDriver(TaxiOrder taxiOrder, int driverId) {
        System.out.println("Taxi Order ID = " + taxiOrder.getId() + "  Driever " + driverId);
        AssembledOrder assembledOrder = AssembledOrder.assembleOrder(taxiOrder);
        List<AssembledRoute> assRoutes = assembledOrder.getAssembledRoutes();
        List<Route> routes = new ArrayList<>();

        for (int j = 0; j < assRoutes.size(); j++) {
            for (Route r : assRoutes.get(j).getRoutes()) {
                if ((r.getDriver() != null) && (driverId == r.getDriver().getId())) {
                    System.out.println("GET CHAIN FOR DIRIVER ID:" + r.getDriver().getId() + " Routes STATUS : " + r.getStatus() +
                            "   ADDRESS : " + r.getSourceAddress() + "  ID: " + r.getId());
                    routes.add(r);

                }
            }
        }
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route r1, Route r2) {
                Calendar s1 = r1.getStartTime();
                Calendar s2 = r2.getStartTime();
                if (s1 == null) {
                    return 1;
                } else if (s2 == null) {
                    return -1;
                }
                return s1.compareTo(s2);
            }
        });
        return routes;
    }

    private boolean isRouteChain(TaxiOrder taxiOrder) {
        //only TaxiAsap and Taxi In Advance can be chain
        if (taxiOrder.getServiceType().isDestinationLocationsChain() != null) {
            return taxiOrder.getServiceType().isDestinationLocationsChain();
        }
        return false;
    }

    // перевірка призначення і прибуття, тільки для роутів != chain
    private Route checkDestSourceParam(List<Route> routes, HttpServletRequest requst) {
        String source, dest;
        if (requst.getParameter("source") != null) {
            source = requst.getParameter("source");
            if ((dest = requst.getParameter("dest")) != null) {
                for (Route r : routes) {
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

        TaxiOrder taxiOrder = routes.get(0).getOrder();
        User user = taxiOrder.getCustomer();
        Object[] obj = {taxiOrder.getExecutionDate(), routes.get(0).getSourceAddress()};
        try {
            mailService.sendNotification(user.getEmail(), Notification.ASSIGNED, obj);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void initInProgressStatus(Route r) {

        Calendar calendar = Calendar.getInstance();
        r.setStatus(RouteStatus.IN_PROGRESS);
        r.setStartTime(calendar);
        Route r1 = r;
        routeService.saveRoute(r1);

        TaxiOrder taxiOrder = r.getOrder();

        User user = taxiOrder.getCustomer();
        Object[] obj = {r.getSourceAddress()};
        try {
            mailService.sendNotification(user.getEmail(), Notification.IN_PROGRESS, obj);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void initCompleteStatus(Route r) {

        Calendar calendar = Calendar.getInstance();
        r.setStatus(RouteStatus.COMPLETED);
        r.setCompletionTime(calendar);
        routeService.saveRoute(r);

        TaxiOrder taxiOrder = r.getOrder();
        User user = taxiOrder.getCustomer();
        Object[] obj = {r.getSourceAddress(), r.getCompletionTime()};
        try {
            mailService.sendNotification(user.getEmail(), Notification.COMPLETED, obj);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void initRefuseStatus(Route r) {
        Calendar calendar = Calendar.getInstance();
        r.setStatus(RouteStatus.REFUSED);
        r.setStartTime(calendar);
        r.setCompletionTime(calendar);
        routeService.saveRoute(r);

        TaxiOrder taxiOrder = r.getOrder();
        User user = taxiOrder.getCustomer();
        Object[] obj = {r.getSourceAddress()};
        try {
            mailService.sendNotification(user.getEmail(), Notification.REFUSED, obj);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
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

    @RequestMapping("/findOrder")
    public void findOrder(@RequestParam("id") long id, HttpServletResponse response) throws IOException {
        response.getWriter().append("" + taxiOrderRepository.findOne(id));
    }
}

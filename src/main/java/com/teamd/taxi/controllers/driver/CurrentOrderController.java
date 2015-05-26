package com.teamd.taxi.controllers.driver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.maps.errors.NotFoundException;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Іван on 02.05.2015.
 */
@Controller
@RequestMapping("/driver")
public class CurrentOrderController {
    private static final Logger log = Logger.getLogger(CurrentOrderController.class);

    @Autowired
    private InfoService infoService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TaxiOrderService taxiOrderService;

    @Autowired
    private ProcessOrderService processOrderService;


    //TODO: hardcode) must take from session
    private int driverId = 6;

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    private String viewQueue(Model model) {
        boolean isActiveOrder = true;
        Driver driver = driverService.getDriver(driverId);
        int drvId = driver.getId();
        TaxiOrder taxiOrder;
        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(drvId)) == null) {
            isActiveOrder = false;
        } else {
            List<Route> sortRoutes = processOrderService.getChainForDriver(taxiOrder, drvId);
            model.addAttribute("sortRoutes", sortRoutes);
        }
        model.addAttribute("isActiveOrder", isActiveOrder);
        return "driver/drv-current-order";
    }


    @RequestMapping(value = "/assign", method = RequestMethod.GET)
    private String assignOrder( @RequestParam MultiValueMap<String, String> params, Model model) {

        Driver driver = driverService.getDriver(driverId);

        try {
            processOrderService.getOrder(params, driver);
        } catch (DriverHasActiveOrderException e) {
            model.addAttribute("errorMessage", "Your already have active order ");
            return "driver/drv-error-page";
        } catch (TaxiOrderNotExist taxiOrderNotExist) {
            model.addAttribute("errorMessage", "Taxi Order not exist");
            return "driver/drv-error-page";
        } catch (DiscrepancyDriverAndOrderFeatureException e) {
            model.addAttribute("errorMessage", "Discrepancy driver and order features");
            return "driver/drv-error-page";
        } catch (OrderBookedException e) {
            model.addAttribute("errorMessage", "Order is book");
            return "driver/drv-error-page";
        } catch (InvalidURLParamException e) {
            model.addAttribute("errorMessage", "Invalid URL Param");
            return "driver/drv-error-page";
        }
        return "redirect:order";
    }

    @RequestMapping(value = "/lifeCircleOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String processOrder(@RequestParam(value = "status") String status){

        JsonObject to = new JsonObject();
        Driver driver =  driverService.getDriver(driverId);
        try {
            to =  processOrderService.processOrder(status, driver);
        } catch (InfoNotFoundException e) {
            //TODO bootstrap alert
        } catch (ItemNotFoundException e) {
            //TODO bootstrap alert
        }
        return new Gson().toJson(to);
    }

    @RequestMapping(value = "/loadAddress")
    public
    @ResponseBody
    String[] loadAddress() {

        Driver driver = driverService.getDriver(driverId);
        TaxiOrder taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId());
        String[] addresses = processOrderService.loadAddress(taxiOrder, driver.getId());

        return addresses;
    }

    @RequestMapping(value = "/setNewRoute", produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String driverCurrentOrder(@RequestParam(value = "source") String source,
                              @RequestParam(value = "destination") String dest){
        JsonObject to = new JsonObject();
        Route route = null;

        try {
            route = processOrderService.newRoute(source, dest, driverId);
        } catch (NotFoundException e) {
            //TODO alert
        } catch (MapServiceNotAvailableException e) {
            //TODO alert
        } catch (TaxiOrderNotExist taxiOrderNotExist) {
            //TODO alert
        } catch (NewRouteNotSupportForOrderException e) {
            //TODO alert
        }

        if ( route != null ){
            to.addProperty("source", route.getSourceAddress());
            to.addProperty("destination", route.getDestinationAddress());
            to.addProperty("status", "ok");
            to.addProperty("routeStatus", route.getStatus().name());
            to.addProperty("id", route.getId());
        } else {
            to.addProperty("status", "fail");
        }
        return new Gson().toJson(to);
    }

    @RequestMapping(value = "/loadCurrentState", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String loadExecuteDate() {

        JsonObject to = new JsonObject();
        Driver driver = driverService.getDriver(driverId);
        TaxiOrder taxiOrder;

        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId())) != null) {
            long idleFreeTime = Long.valueOf(infoService.getIdleFreeTime("idle_free_time").getValue()) * 3000;
            long executeOrderDate = taxiOrder.getExecutionDate().getTimeInMillis();

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
                    if ((completionDate = processOrderService.getTimeOfLastCompletionRouteInChain(taxiOrder, driver.getId())) == null) {
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

}

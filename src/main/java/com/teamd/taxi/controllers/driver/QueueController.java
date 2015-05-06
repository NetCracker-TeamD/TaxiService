package com.teamd.taxi.controllers.driver;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.service.DriverService;
import com.teamd.taxi.service.ServiceTypeService;
import com.teamd.taxi.service.TaxiOrderService1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Іван on 02.05.2015.
 */
@SessionAttributes( types = Driver.class)
@Controller
@RequestMapping("/driver")
public class QueueController {

    private String SORT_BY = "executionDate";
    private int PAGE_SIZE = 20;
    private static Pageable pageableOrder;
    private int curPage = 0;
    private List<ServiceType> serviceTypes;
    //  RouteStatus.COMPLETED видалити, в базі не було даних з RouteStatus.QUEUED, RouteStatus.UPDATED
    private  List<RouteStatus> statusList = Arrays.asList(RouteStatus.COMPLETED,
                                                          RouteStatus.QUEUED,
                                                          RouteStatus.UPDATED);

    @Autowired
    private TaxiOrderService1 taxiOrderService1;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private DriverService driverService;

    @RequestMapping(value ="/queue", method = RequestMethod.GET)
    public String viewCurrentOrder(Model model, HttpServletRequest request) {

        Driver registerDriver = driverService.getDriver(1);

        if(request.getParameter("curPage") != null){
            curPage = Integer.parseInt(request.getParameter("curPage"))-1;
        }
        pageableOrder = new PageRequest(curPage,PAGE_SIZE, Sort.Direction.ASC, SORT_BY);
        serviceTypes = serviceTypeService.getAllService();
        Page<TaxiOrder> orders= taxiOrderService1.getFreeOrder(statusList, pageableOrder);

        model.addAttribute("services", serviceTypes);
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("countPage", orders.getTotalPages());
        return "driver/drv-queue";
    }

    @RequestMapping(value ="/queue", method = RequestMethod.POST)
    public String viewFilterSrviceOrder(Model model, HttpServletRequest request) {
        List<Integer> idService = new ArrayList<>();

        for (ServiceType service: serviceTypes){
            if(request.getParameter(service.getId().toString()) != null){
                idService.add(service.getId());
            }
        }
        if(idService.size() == 0){
//            redirect error page
        }
        Page<TaxiOrder> orders= taxiOrderService1.getFilterServiceFreeOrders(statusList, idService, pageableOrder);
        model.addAttribute("services", serviceTypes);
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("countPage", orders.getTotalPages());
        return "driver/drv-queue";
    }


}

package com.teamd.taxi.controllers.driver;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.service.TaxiOrderService;
import com.teamd.taxi.service.TaxiOrderSpecificationFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/driver")
public class HistoryDriverController {
    @Autowired
    TaxiOrderService orderService;
    @Autowired
    private TaxiOrderSpecificationFactory factory;

    Logger logger=Logger.getLogger(HistoryDriverController.class);
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String viewHistory(Model model, @RequestParam Map<String,String> requestParam) {
        int page=0;
        if(requestParam.get("page")!=null)
            page=Integer.parseInt(requestParam.get("page"))-1;

        String sort = checkSort(requestParam.get("sort"));
        int numberOfRows = 7;
        int idDriver=2;
        Pageable pageable=new PageRequest(page,numberOfRows, Sort.Direction.ASC, sort);
        Page<TaxiOrder> orderList = orderService.findTaxiOrderByDriver(
                resolveSpecification(requestParam, idDriver),
                pageable
        );
        if(orderList==null){
            //redirect error page
        }
        List<TaxiOrder> orders = orderList.getContent();
        setFilteringOFRoutesForDriver(orders, idDriver);
        model.addAttribute("orderList", orders);
        model.addAttribute("pages", orderList.getTotalPages());
        model.addAttribute("serviceTypes", allowedServiceType);
        return "driver/drv-history";
    }
    private void checkParams(Map<String,String> requestParam){
        if(requestParam.get("sort")!=null) {
            requestParam.put("sort", checkSort(requestParam.get("sort")));
        }
    }
    private Specification<TaxiOrder> resolveSpecification(Map<String, String> params,int idDriver) {
        List<Specification<TaxiOrder>> specs = new ArrayList<>();
        //to date
        String to_date= params.get("startDate");
        if (to_date != null) {
            try {
                specs.add(factory.executionDateGreaterThan(getCalendarByStr(to_date)));
            } catch (NumberFormatException exception) {
                logger.error("error from startDate");
            }
        }
        //from date
        String from_date= params.get("endDate");
        if (from_date != null) {
            try {
                specs.add(factory.executionDateLessThan(getCalendarByStr(from_date)));
            } catch (NumberFormatException exception) {
                logger.error("error from endDate");
            }
        }
        String service_type=params.get("service_type");
        if(service_type!=null){
            if(allowedServiceType.containsKey(service_type)){
                specs.add(factory.serviceTypeEqual(allowedServiceType.get(service_type)));
            }
        }
        if(params.get("id_order")!=null){
            int id_order=Integer.parseInt(params.get("id_order"));
            specs.add(factory.taxiOrderEqual(id_order));
        }
        String address=params.get("address");
        if(address!=null){
            specs.add(factory.sourceOrDestinationAddressLike(address));
        }
        specs.add(factory.statusRouteEqual(RouteStatus.COMPLETED));
        specs.add(factory.driverIdEqual(idDriver));

        //join specifications
        if (specs.size() != 0) {
            Iterator<Specification<TaxiOrder>> specIt = specs.iterator();
            Specifications<TaxiOrder> spec = Specifications.where(specIt.next());
            while (specIt.hasNext()) {
                spec = spec.and(specIt.next());
            }
            return spec;
        }
        return null;
    }
    private Calendar getCalendarByStr(String strDate){
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal=Calendar.getInstance(Locale.ROOT);
        try {
             cal.setTimeInMillis(f.parse(strDate).getTime());
        } catch (ParseException e) {
            logger.error("Bad format date");
        }
        return cal;
    }
    private String checkSort(String sort){
        if(sort==null){
            return "id";
        }
        if(sort.equals("date")){
            return "executionDate";
        }else
            return "id";
    }
    private void setFilteringOFRoutesForDriver(List<TaxiOrder> orders,int idDriver){
        for(TaxiOrder order:orders){
            List<Route> routes=order.getRoutes();

            Iterator<Route> i=routes.iterator();
            while(i.hasNext()){
                Route r=i.next();
                if(r.getDriver().getId()!=idDriver){
                    i.remove();
                }
            }
            routes.sort(new Comparator<Route>() {
                @Override
                public int compare(Route o1, Route o2) {
                    if(o1.getStartTime().compareTo(o2.getStartTime())==1){
                        return 1;
                    }else if(o1.getStartTime().compareTo(o2.getStartTime())==-1){
                        return -1;
                    }
                    return 0;
                }
            });
        }
    }
    private Map<String,Integer> allowedServiceType=new HashMap<String,Integer>(){{
        put("Taxi asap",1);
        put("Taxi in advance",2);
        put("Sober driver",3);
        put("Convey corp. emps.",4);
        put("Cargo taxi",5);
        put("Taxi for long term",6);
        put("Meet my guest",7);
        put("Celebration taxi",8);
        put("Foodstuff delivery",9);
        put("Guest Delivery",10);
    }};
}

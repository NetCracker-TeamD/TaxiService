package com.teamd.taxi.controllers.driver;

import com.teamd.taxi.authentication.AuthenticatedUser;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.service.DriverService;
import com.teamd.taxi.service.ServiceTypeService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired
    private ServiceTypeService service;

    @Autowired
    private DriverService driverService;

    private List<ServiceType> typeList;

    Logger logger = Logger.getLogger(HistoryDriverController.class);

    @RequestMapping(value = "/history/{driverId}", method = RequestMethod.GET)
    public String getDriverHistoryById(Model model, @RequestParam Map<String, String> requestParam, @PathVariable int driverId) {
        Driver driver = driverService.getDriver(driverId);
        setViewHistory(model, requestParam, driver, "ROLE_ADMINISTRATOR");
        return "driver/drv-history";
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String getCurrentDriverHistory(Model model, @RequestParam Map<String, String> requestParam) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        AuthenticatedUser auth = (AuthenticatedUser) authentication.getPrincipal();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DRIVER"))) {
            Driver driver = driverService.getDriver((int) auth.getId());
            setViewHistory(model, requestParam, driver, "ROLE_DRIVER");
            return "driver/drv-history";
        }
        return null;
    }
    private void setViewHistory(Model model, Map<String, String> requestParam, Driver driver, String role ) {
        int page = 0;
        if (requestParam.get("page") != null)
            page = Integer.parseInt(requestParam.get("page")) - 1;
        Sort sort = checkSort(requestParam.get("sort"));
        int numberOfRows = 7;
        typeList = service.findAll();
        Pageable pageable = new PageRequest(page, numberOfRows, sort);
        Page<TaxiOrder> orderList = orderService.findTaxiOrderByDriver(
                resolveSpecification(requestParam, driver.getId()),
                pageable
        );
        if (orderList == null) {
            //TODO redirect error page
        }
        List<TaxiOrder> orders = orderList.getContent();
        setFilteringOFRoutesForDriver(orders, driver.getId());
        model.addAttribute("orderList", orders);
        model.addAttribute("pages", orderList.getTotalPages());
        model.addAttribute("serviceTypes", typeList);
        model.addAttribute("role", role);
        model.addAttribute("driver_id", driver.getId());
    }

    private Specification<TaxiOrder> resolveSpecification(Map<String, String> params, int idDriver) {
        List<Specification<TaxiOrder>> specs = new ArrayList<>();
        //to date
        String to_date = params.get("startDate");
        if (to_date != null) {
            try {
                specs.add(factory.executionDateGreaterThan(getCalendarByStr(to_date+" 00:00")));
            } catch (NumberFormatException exception) {
                logger.error("error from startDate");
            }
        }
        //from date
        String from_date = params.get("endDate");
        if (from_date != null) {
            try {
                specs.add(factory.executionDateLessThan(getCalendarByStr(from_date+" 23:59")));
            } catch (NumberFormatException exception) {
                logger.error("error from endDate");
            }
        }
        String service_type = params.get("service_type");
        if (service_type != null) {
            ServiceType service = getServiceType(service_type);
            if (service != null) {
                specs.add(factory.serviceTypeEqual(service.getId()));
            }
        }
        if (params.get("id_order") != null) {
            int id_order = Integer.parseInt(params.get("id_order"));
            specs.add(factory.taxiOrderEqual(id_order));
        }
        String address = params.get("address");
        if (address != null) {
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

    private Calendar getCalendarByStr(String strDate) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar cal = Calendar.getInstance(Locale.ROOT);
        try {
            cal.setTimeInMillis(f.parse(strDate).getTime());
        } catch (ParseException e) {
            logger.error("Bad format date");
        }
        return cal;
    }

    private ServiceType getServiceType(String name_type) {
        for (ServiceType serviceType : typeList) {
            if (serviceType.getName().equals(name_type)) {
                return serviceType;
            }
        }
        return null;
    }

    private Sort checkSort(String sort) {
        if (sort == null||sort.equals("newest")) {
            return new Sort(Sort.Direction.DESC,"executionDate");
        }else if (sort.equals("oldest")) {
            return new Sort(Sort.Direction.ASC,"executionDate");
        } else
            return new Sort(Sort.Direction.DESC,"executionDate");
    }

    private void setFilteringOFRoutesForDriver(List<TaxiOrder> orders, int idDriver) {
        for (TaxiOrder order : orders) {
            List<Route> routes = order.getRoutes();

            Iterator<Route> i = routes.iterator();
            while (i.hasNext()) {
                Route r = i.next();
                if (r.getDriver().getId() != idDriver) {
                    i.remove();
                }
            }
            Collections.sort(routes, new Comparator<Route>() {
                @Override
                public int compare(Route o1, Route o2) {
                    if (o1.getStartTime().compareTo(o2.getStartTime()) == 1) {
                        return 1;
                    } else if (o1.getStartTime().compareTo(o2.getStartTime()) == -1) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
    }
}

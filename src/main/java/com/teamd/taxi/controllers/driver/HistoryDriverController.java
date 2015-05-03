package com.teamd.taxi.controllers.driver;

import javax.servlet.http.HttpServletRequest;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.service.TaxiOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/driver")
public class HistoryDriverController {
    @Autowired
    TaxiOrderService orderService;
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String viewHistory(Model model, HttpServletRequest request) {
        int page=0;
        String sort="id";
        if(request.getParameter("page")!=null){
            page=Integer.parseInt(request.getParameter("page"))-1;
        }
        if(request.getParameter("sort")!=null){
            switch (request.getParameter("sort")){
                case "date":
                    sort = "executionDate";
                    break;
                case "id":
                    sort = "id";
                    break;
            }
        }
        int numberOfRows = 7;
        int idDriver=7;
        Pageable pageable=new PageRequest(page,numberOfRows, Sort.Direction.ASC, sort);
        Page<TaxiOrder> orderList= orderService.findTaxiOrderByDriver(idDriver, pageable);
        if(orderList==null){
            //redirect error page
        }
        List<TaxiOrder> orders = orderList.getContent();
        List<Float> prices = new ArrayList<Float>();
        for (TaxiOrder order : orders) {
            float price = 0.0f;
            for (Route route : order.getRoutes()) {
                if(route.getTotalPrice()!=null) {
                    price += route.getTotalPrice();
                }
            }
            prices.add(price);
        }
        model.addAttribute("orderList", orderList.getContent());
        model.addAttribute("prices",prices);
        model.addAttribute("pages", orderList.getTotalPages());
        return "driver/drv-history";
    }
}

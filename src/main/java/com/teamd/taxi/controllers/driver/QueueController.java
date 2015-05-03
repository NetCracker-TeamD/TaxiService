package com.teamd.taxi.controllers.driver;

import com.teamd.taxi.entity.PaymentType;
import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Іван on 02.05.2015.
 */
@Controller
@RequestMapping("/driver")
public class QueueController {

    @Autowired
    private TaxiOrderRepository taxiOrderRepository;


    @RequestMapping(value = "/queue", method = RequestMethod.GET)
    public String viewCurrentOrder(Model model, HttpServletRequest request) {
        Object filter = new Object();
        List<TaxiOrder> orders = getListOrders(filter);
        model.addAttribute("orderList", orders);
        return "driver/drv-queue";
    }

    List<TaxiOrder> getListOrders(Object filter) {
        List<TaxiOrder> orders = new ArrayList<TaxiOrder>();
        for (int i = 0; i < 20; i++) {
            TaxiOrder order = taxiOrderRepository.findOne((long) i);
            orders.add(order);
        }
        return orders;
    }
}

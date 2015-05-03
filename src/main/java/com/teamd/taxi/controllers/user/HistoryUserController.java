package com.teamd.taxi.controllers.user;

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

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Anton on 29.04.2015.
 */
@Controller
@RequestMapping("/user")
public class HistoryUserController {
    @Autowired
    TaxiOrderService orderService;

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String viewHistory(Model model, HttpServletRequest request) {
        int page = 0;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }
        int idUser = 1;
        int numberOfRows = 7;
        String sort = "id";
        if (request.getParameter("sort") != null) {
            switch (request.getParameter("sort")) {
                case "date":
                    sort = "executionDate";
                    break;
                case "id":
                    sort = "id";
                    break;
            }
        }
        Pageable pageable = new PageRequest(page, numberOfRows, Sort.Direction.ASC, sort);
        Page<TaxiOrder> orderList = orderService.findTaxiOrderByUser(idUser, pageable);
        if (orderList == null) {
            //redirect error page
        }
        model.addAttribute("orderList", orderList.getContent());
        model.addAttribute("pages", orderList.getTotalPages());
        return "user-history";
    }

}

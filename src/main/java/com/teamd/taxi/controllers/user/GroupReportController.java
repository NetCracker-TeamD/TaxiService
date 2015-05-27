package com.teamd.taxi.controllers.user;

import com.teamd.taxi.authentication.Utils;
import com.teamd.taxi.entity.GroupList;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.exception.ItemNotFoundException;
import com.teamd.taxi.models.MapResponse;
import com.teamd.taxi.persistence.repository.ReportsRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
import com.teamd.taxi.service.CustomerUserService;
import com.teamd.taxi.service.GroupsService;
import com.teamd.taxi.service.ReportResolver;
import com.teamd.taxi.service.UserReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user")
public class GroupReportController {


    @Autowired
    CustomerUserService userService;

    @Autowired
    GroupsService groupsService;

    @Autowired
    UserReportService reportService;

    @RequestMapping(value = "/statistic")
    public String viewStatistic(@RequestParam("group") int groupId, Model model) {
        String message;
        try {
            if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
                model.addAttribute("groupId", groupId);
                return "user/statistic";
            }
            message = "Access to group statistic denied";
        } catch (ItemNotFoundException ex) {
            message = "Group not found";
        }
        model.addAttribute("message", message);
        return "user/statistic-error";
    }

    @RequestMapping("/group")
    @Transactional
    public String viewGroups(Model model) {
        List<UserGroup> groups = reportService.findGroupForUser(Utils.getCurrentUser().getId());
        model.addAttribute("groups", groups);
        return "user/group_view";
    }

    @RequestMapping(value = "/statistic/newOrdersPerPeriod", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object generateNewOrdersPerPeriodReport(@RequestParam("startDate") final String startDate,
                                                   @RequestParam("endDate") final String endDate,
                                                   @RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            return reportService.getNewOrders(startDate, endDate, groupId);
        }
        return new MapResponse().put("error", "isNotManager");
    }


    @RequestMapping(value = "/statistic/mostPopularAdditionalCarOptionsOverall")
    @ResponseBody
    public Object generateMostPopularAdditionalCarOptionsOverallReport(@RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            return reportService.getAdditionalOptions(groupId);
        }
        return new MapResponse().put("error", "isNotManager");

    }

    @RequestMapping(value = "/statistic/mostPopularAdditionalCarOptionsForEachCustomerUser")
    @ResponseBody
    public Object generateMostPopularAdditionalCarOptionsForEachCustomerUserReport(@RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            return reportService.getAdditionalOptionsForUser(groupId);
        }
        return new MapResponse().put("error", "isNotManager");
    }

    @RequestMapping(value = "/statistic/mostPopularCar")
    @ResponseBody
    public Object generateMostPopularCarReport(@RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            return reportService.getPopularCar(groupId);
        }
        return new MapResponse().put("error", "isNotManager");
    }

    @RequestMapping(value = "/statistic/mostProfitableService")
    @ResponseBody
    public Object generateMostProfitableServiceReport(@RequestParam("period") final String period,
                                                      @RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            return reportService.getProfitByPeriod(period, groupId);
        }
        return new MapResponse().put("error", "isNotManager");
    }

    @RequestMapping(value = "/statistic/serviceProfitabilityByMonth")
    @ResponseBody
    public Object generateServiceProfitabilityReport(@RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            return reportService.getServiceProfitability(groupId);
        }
        return new MapResponse().put("error", "isNotManager");
    }

    @RequestMapping(value = "/statistic/exportServiceProfitability", method = RequestMethod.GET)
    public ModelAndView getExcelServiceProfitability(@RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            List report = reportService.getServiceProfitabilityList(reportService.getServiceProfitability(groupId));
            return new ModelAndView("serviceProfitability", "report", report);
        }
        ModelAndView mav = new ModelAndView("user/statistic-error");
        String message = "Access to group statistic denied";
        mav.addObject("message", message);
        return mav;
    }

    @RequestMapping(value = "/statistic/exportMostPopularCar", method = RequestMethod.GET)
    public ModelAndView getExcelMostPopularCar(@RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            List report = reportService.getPopularCarList(reportService.getPopularCar(groupId));
            return new ModelAndView("popularCars", "report", report);
        }
        ModelAndView mav = new ModelAndView("user/statistic-error");
        String message = "Access to group statistic denied";
        mav.addObject("message", message);
        return mav;
    }

    @RequestMapping(value = "/statistic/exportAdditionalOptions", method = RequestMethod.GET)
    public ModelAndView getExcelAdditionalOptions(@RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            List report = reportService.getAdditionalOptionsList(reportService.getAdditionalOptions(groupId));
            return new ModelAndView("options", "report", report);
        }
        ModelAndView mav = new ModelAndView("user/statistic-error");
        String message = "Access to group statistic denied";
        mav.addObject("message", message);
        return mav;
    }

    @RequestMapping(value = "/statistic/exportAdditionalOptionsForUser", method = RequestMethod.GET)
    public ModelAndView getExcelAdditionalOptionsForUser(@RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            List report = reportService.getAdditionalOptionsForUserList(reportService.getAdditionalOptionsForUser(groupId));
            return new ModelAndView("optionsForUser", "report", report);
        }
        ModelAndView mav = new ModelAndView("user/statistic-error");
        String message = "Access to group statistic denied";
        mav.addObject("message", message);
        return mav;
    }

    @RequestMapping(value = "/statistic/exportNewOrders", method = RequestMethod.GET)
    public ModelAndView getExcelNewOrders(@RequestParam("startDate") final String startDate,
                                          @RequestParam("endDate") final String endDate,
                                          @RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            List report = reportService.getNewOrderList(reportService.getNewOrders(startDate, endDate, groupId));
            return new ModelAndView("newOrders", "report", report);
        }
        ModelAndView mav = new ModelAndView("user/statistic-error");
        String message = "Access to group statistic denied";
        mav.addObject("message", message);
        return mav;
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseBody
    public MapResponse handleException(Exception ex) {
        return new MapResponse().put("message", ex.getMessage())
                .put("error", ex.getClass());
    }

    @RequestMapping(value = "/statistic/exportMostProfitable", method = RequestMethod.GET)
    public ModelAndView getExcelMostProfitable(@RequestParam("period") String period,
                                               @RequestParam("group") int groupId) throws ItemNotFoundException {
        if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
            List report = reportService.getProfitByPeriodList(reportService.getProfitByPeriod(period, groupId));
            return new ModelAndView("mostProfitable", "report", report);
        }

        ModelAndView mav = new ModelAndView("user/statistic-error");
        String message = "Access to group statistic denied";
        mav.addObject("message", message);
        return mav;
    }


}

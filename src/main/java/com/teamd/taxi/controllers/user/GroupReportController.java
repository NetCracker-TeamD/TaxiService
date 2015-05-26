package com.teamd.taxi.controllers.user;

import com.teamd.taxi.authentication.Utils;
import com.teamd.taxi.entity.GroupList;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.exception.ItemNotFoundException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    long groupId;

    @Autowired
    CustomerUserService userService;

    @Autowired
    GroupsService groupsService;

    @Autowired
    UserReportService reportService;

    @RequestMapping(value = "/statistic")
    public String viewStatistic(@RequestParam("group") int groupId, Model model) {
        this.groupId = groupId;
        String message;
        try {
            if (groupsService.isManager(Utils.getCurrentUser().getId(), groupId)) {
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

    @RequestMapping(value = "/statistic/newOrdersPerPeriod")
    @ResponseBody
    public List<Map<String, Object>> generateNewOrdersPerPeriodReport(@RequestParam("startDate") final String startDate,
                                                                      @RequestParam("endDate") final String endDate) {
        return reportService.getNewOrders(startDate, endDate, groupId);
    }


    @RequestMapping(value = "/statistic/mostPopularAdditionalCarOptionsOverall")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularAdditionalCarOptionsOverallReport() {
        return reportService.getAdditionalOptions(groupId);
    }

    @RequestMapping(value = "/statistic/mostPopularAdditionalCarOptionsForEachCustomerUser")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularAdditionalCarOptionsForEachCustomerUserReport() {
        return reportService.getAdditionalOptionsForUser(groupId);
    }

    @RequestMapping(value = "/statistic/mostPopularCar")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularCarReport() {
        return reportService.getPopularCar(groupId);
    }

    @RequestMapping(value = "/statistic/mostProfitableService")
    @ResponseBody
    public List<Map<String, Object>> generateMostProfitableServiceReport(@RequestParam("period") final String period) {
        return reportService.getProfitByPeriod(period, groupId);
    }

    @RequestMapping(value = "/statistic/serviceProfitabilityByMonth")
    @ResponseBody
    public List<Map<String, Object>> generateServiceProfitabilityReport() {
        return reportService.getServiceProfitability(groupId);
    }

    @RequestMapping(value = "/statistic/exportServiceProfitability", method = RequestMethod.GET)
    public ModelAndView getExcelServiceProfitability() {
        List report = reportService.getServiceProfitabilityList(reportService.getServiceProfitability(groupId));
        return new ModelAndView("serviceProfitability", "report", report);
    }

    @RequestMapping(value = "/statistic/exportMostPopularCar", method = RequestMethod.GET)
    public ModelAndView getExcelMostPopularCar() {
        List report = reportService.getPopularCarList(reportService.getPopularCar(groupId));
        return new ModelAndView("popularCars", "report", report);
    }

    @RequestMapping(value = "/statistic/exportAdditionalOptions", method = RequestMethod.GET)
    public ModelAndView getExcelAdditionalOptions() {
        List report = reportService.getAdditionalOptionsList(reportService.getAdditionalOptions(groupId));
        return new ModelAndView("options", "report", report);
    }

    @RequestMapping(value = "/statistic/exportAdditionalOptionsForUser", method = RequestMethod.GET)
    public ModelAndView getExcelAdditionalOptionsForUser() {
        List report = reportService.getAdditionalOptionsForUserList(reportService.getAdditionalOptionsForUser(groupId));
        return new ModelAndView("optionsForUser", "report", report);
    }

    @RequestMapping(value = "/statistic/exportNewOrders", method = RequestMethod.GET)
    public ModelAndView getExcelNewOrders(@RequestParam("startDate") final String startDate,
                                          @RequestParam("endDate") final String endDate) {
        if (isValidDate(startDate) && isValidDate(endDate)) {
            List report = reportService.getNewOrderList(reportService.getNewOrders(startDate, endDate, groupId));
            return new ModelAndView("newOrders", "report", report);
        } else {
            RedirectView view = new RedirectView("/user/statistic", true);
            view.setExpandUriTemplateVariables(false);
            Map params = new HashMap();
            params.put("group", groupId);
            view.setAttributesMap(params);
            return new ModelAndView(view);
        }

    }

    @RequestMapping(value = "/statistic/exportMostProfitable", method = RequestMethod.GET)
    public ModelAndView getExcelMostProfitable(@RequestParam("period") String period) {
        List report = reportService.getProfitByPeriodList(reportService.getProfitByPeriod(period, groupId));
        return new ModelAndView("mostProfitable", "report", report);
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }


}

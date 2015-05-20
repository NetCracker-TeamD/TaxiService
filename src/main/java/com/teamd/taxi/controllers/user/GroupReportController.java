package com.teamd.taxi.controllers.user;

import com.teamd.taxi.entity.GroupList;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.persistence.repository.ReportsRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
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

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user")
public class GroupReportController {

    //TODO insert authorized user
    long userId = 7;
    long groupId;


    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupsService groupsService;

    @Autowired
    UserReportService reportService;

    @RequestMapping(value = "/statistic")
    public String viewStatistic(@RequestParam("group") int groupId) {
        this.groupId = groupId;
        if (groupsService.isManager(userId, groupId)){
            return "user/statistic";
        }else {
            return "user/statistic-error";
        }
    }

    @RequestMapping("/group")
    @Transactional
    public String viewGroups(Model model) {
        List<UserGroup> groups = groupsService.getGroupsList();
        User user = userRepository.findOne(userId);
        List<GroupList> userGroups = groupsService.getGroupForUser(user);
        List<UserGroup> result = new ArrayList<>();
        for (UserGroup group : groups) {
            for (GroupList userGroup : userGroups) {
                if (userGroup.getUserGroup().equals(group)) {
                    result.add(group);
                }
            }
        }
        model.addAttribute("groups", result);
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
        List report = reportService.getNewOrderList(reportService.getNewOrders(startDate, endDate, groupId));
        return new ModelAndView("newOrders", "report", report);
    }

    @RequestMapping(value = "/statistic/exportMostProfitable", method = RequestMethod.GET)
    public ModelAndView getExcelMostProfitable(@RequestParam("period") String period) {
        List report = reportService.getProfitByPeriodList(reportService.getProfitByPeriod(period, groupId));
        return new ModelAndView("mostProfitable", "report", report);
    }

}

package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.service.AdminReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;


@Controller
@RequestMapping("admin/statistic")
public class AdminReportsController {

    @Autowired
    private AdminReportService adminReportService;

    @RequestMapping(value = "/serviceProfitabilityByMonth")
    @ResponseBody
    public List<Map<String, Object>> generateServiceProfitabilityReport() {
        return adminReportService.getServiceProfitability();
    }

    @RequestMapping(value = "/newOrdersPerPeriod")
    @ResponseBody
    public List<Map<String, Object>> generateNewOrdersPerPeriodReport(@RequestParam("startDate") final String startDate,
                                                                      @RequestParam("endDate") final String endDate) {
        return adminReportService.getNewOrders(startDate, endDate);
    }

    @RequestMapping(value = "/mostPopularAdditionalCarOptionsOverall")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularAdditionalCarOptionsOverallReport() {
        return adminReportService.getAdditionalOptions();
    }

    @RequestMapping(value = "/mostPopularAdditionalCarOptionsForEachCustomerUser")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularAdditionalCarOptionsForEachCustomerUserReport() {
        return adminReportService.getAdditionalOptionsForUser();
    }

    @RequestMapping(value = "/mostPopularCar")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularCarReport() {
        return adminReportService.getPopularCar();
    }


    @RequestMapping(value = "/mostProfitableService")
    @ResponseBody
    public List<Map<String, Object>> generateMostProfitableServiceReport(@RequestParam("period") String period) {
        return adminReportService.getProfitByPeriod(period);
    }

    @RequestMapping
    public ModelAndView viewStatistic() {
        return new ModelAndView("admin/statistic");
    }

    @RequestMapping(value = "/exportServiceProfitability", method = RequestMethod.GET)
    public ModelAndView getExcelServiceProfitability() {
        List report = adminReportService.getServiceProfitabilityList(adminReportService.getServiceProfitability());
        return new ModelAndView("serviceProfitability", "report", report);
    }

    @RequestMapping(value = "/exportMostPopularCar", method = RequestMethod.GET)
    public ModelAndView getExcelMostPopularCar() {
        List report = adminReportService.getPopularCarList(adminReportService.getPopularCar());
        return new ModelAndView("popularCars", "report", report);
    }

    @RequestMapping(value = "/exportAdditionalOptions", method = RequestMethod.GET)
    public ModelAndView getExcelAdditionalOptions() {
        List report = adminReportService.getAdditionalOptionsList(adminReportService.getAdditionalOptions());
        return new ModelAndView("options", "report", report);
    }

    @RequestMapping(value = "/exportAdditionalOptionsForUser", method = RequestMethod.GET)
    public ModelAndView getExcelAdditionalOptionsForUser() {
        List report = adminReportService.getAdditionalOptionsForUserList(adminReportService.getAdditionalOptionsForUser());
        return new ModelAndView("optionsForUser", "report", report);
    }

    @RequestMapping(value = "/exportNewOrders", method = RequestMethod.GET)
    public ModelAndView getExcelNewOrders(@RequestParam("startDate") final String startDate,
                                          @RequestParam("endDate") final String endDate) {
        List report = adminReportService.getNewOrderList(adminReportService.getNewOrders(startDate, endDate));
        return new ModelAndView("newOrders", "report", report);
    }


    @RequestMapping(value = "/exportMostProfitable", method = RequestMethod.GET)
    public ModelAndView getExcelMostProfitable(@RequestParam("period") String period) {
        List report = adminReportService.getProfitByPeriodList(adminReportService.getProfitByPeriod(period));
        return new ModelAndView("mostProfitable", "report", report);
    }

}

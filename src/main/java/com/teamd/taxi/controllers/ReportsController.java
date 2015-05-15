package com.teamd.taxi.controllers;

import com.teamd.taxi.persistence.repository.ReportsRepository;
import com.teamd.taxi.service.ReportResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.*;

@Controller
@RequestMapping("/statistic")
public class ReportsController {

    @Autowired
    private ReportsRepository reportsRepository;

    @RequestMapping(value = "/serviceProfitabilityByMonth")
    @ResponseBody
    public List<Map<String, Object>> generateServiceProfitabilityReport() {

        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT " +
                        "EXTRACT(MONTH FROM  route.start_time) as cur_month, " +
                        "EXTRACT (YEAR FROM  route.start_time) as cur_year, sum(total_price) as total " +
                        "FROM route " +
                        "WHERE route.start_time IS NOT NULL " +
                        "GROUP BY cur_month,cur_year  " +
                        "ORDER BY cur_month desc";
            }


            @Override
            public RowMapper getRowMapper() {
                final String[] NAMES = {"January", "February", "March", "April", "May", "June", "July", "August",
                        "September", "October", "November", "December"};
                RowMapper<Map<String, Object>> mapper = new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        String year = resultSet.getString("cur_year");
                        int month = resultSet.getInt("cur_month");
                        String monthName = NAMES[month - 1];
                        String total = resultSet.getString("total");
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("Period of time", monthName + ", " + year);
                        result.put("Profit", total);
                        return result;
                    }
                };
                return mapper;
            }

            @Override
            public Object[] getParams() {
                return null;
            }
        });
    }

    @RequestMapping(value = "/newOrdersPerPeriod")
    @ResponseBody
    public List<Map<String, Object>> generateNewOrdersPerPeriodReport(@RequestParam("startDate") final String startDate,
                                                                      @RequestParam("endDate")final String  endDate) {
        final String from=startDate+" 00:00:00";
        final String to=endDate+" 00:00:00";
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT \"user\".last_name as user_last_name,\"user\".first_name as user_first_name," +
                        "driver.last_name as driver_last_name,driver.first_name as driver_first_name," +
                        "service_type.name as service_name,route.total_price as profit " +
                        "FROM \"user\" " +
                        "JOIN taxi_order ON taxi_order.user_id=\"user\".id " +
                        "JOIN route ON route.order_id=taxi_order.id " +
                        "JOIN driver ON driver.id=route.driver_id " +
                        "JOIN service_type ON service_type.id=taxi_order.service_type " +
                        "WHERE taxi_order.execution_date BETWEEN ? AND ?";
            }

            @Override
            public RowMapper getRowMapper() {
                return new RowMapper() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        String userLastName = resultSet.getString("user_last_name");
                        String userFirstName = resultSet.getString("user_first_name");
                        String driverLastName = resultSet.getString("driver_last_name");
                        String driverFirstName = resultSet.getString("driver_first_name");
                        String serviceTypeName = resultSet.getString("service_name");
                        String totalPrice = resultSet.getString("profit");
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("User name", userFirstName + " " + userLastName);
                        result.put("Driver name", driverFirstName + " " + driverLastName);
                        result.put("Service name", serviceTypeName);
                        result.put("Profit", totalPrice);
                        return result;
                    }
                };
            }
            @Override
            public Object[] getParams() {
                return new Object[]{Timestamp.valueOf(from), Timestamp.valueOf(to)};

            }
        });
    }

    @RequestMapping(value = "/mostPopularAdditionalCarOptionsOverall")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularAdditionalCarOptionsOverallReport() {
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT feature.name as feature_name,COUNT(order_features.feature_id) as usage " +
                        "FROM order_features " +
                        "JOIN feature ON order_features.feature_id=feature.id " +
                        "GROUP BY feature.name " +
                        "ORDER BY usage desc";
            }

            @Override
            public RowMapper getRowMapper() {

                return new RowMapper() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        String featureName = resultSet.getString("feature_name");
                        String usage = resultSet.getString("usage");
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("Feature name", featureName);
                        result.put("Count of usage", usage);
                        return result;
                    }
                };
            }

            @Override
            public Object[] getParams() {
                return null;
            }
        });
    }

    @RequestMapping(value = "/mostPopularAdditionalCarOptionsForEachCustomerUser")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularAdditionalCarOptionsForEachCustomerUserReport() {
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT title,max(feature_count)as max_count, user_name,user_surname " +
                        "FROM " +
                        "   (SELECT  feature.name as title,count(order_features.feature_id) AS feature_count," +
                        "   \"user\".first_name as user_name,\"user\".last_name as user_surname " +
                        "   FROM order_features  " +
                        "   JOIN feature ON order_features.feature_id=feature.id  " +
                        "   JOIN taxi_order ON order_features.order_id=taxi_order.id " +
                        "   JOIN \"user\" ON \"user\".id=taxi_order.user_id " +
                        "   GROUP BY \"user\".first_name,\"user\".last_name, feature.name " +
                        "   ORDER by \"user\".first_name,\"user\".last_name" +
                        "   )as temporary_table " +
                        "GROUP BY user_name,user_surname,title " +
                        "ORDER BY max_count desc";
            }

            @Override
            public RowMapper getRowMapper() {
                return new RowMapper() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        String title = resultSet.getString("title");
                        String featureCount = resultSet.getString("max_count");
                        String userName = resultSet.getString("user_name");
                        String userSurname = resultSet.getString("user_surname");
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("Feature name", title);
                        result.put("Count of usage", featureCount);
                        result.put("User name", userName + " " + userSurname);
                        return result;
                    }
                };
            }

            @Override
            public Object[] getParams() {
                return null;
            }
        });
    }

    @RequestMapping(value = "/mostPopularCar")
    @ResponseBody
    public List<Map<String, Object>> generateMostPopularCarReport() {

        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT car.model, car_class.class_name,COUNT(taxi_order.id) AS usage " +
                        "FROM car  " +
                        "JOIN car_class ON car.car_class_id=car_class.id " +
                        "JOIN driver ON driver.id=car.driver_id " +
                        "JOIN route ON driver.id=route.driver_id " +
                        "JOIN taxi_order ON route.order_id=taxi_order.id " +
                        "GROUP BY car.model, car_class.class_name " +
                        "ORDER BY usage DESC";
            }

            @Override
            public RowMapper getRowMapper() {
                RowMapper<Map<String, Object>> mapper = new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        String model = resultSet.getString("model");
                        String carClass = resultSet.getString("class_name");
                        String usage = resultSet.getString("usage");
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("Car model", model);
                        result.put("Car class", carClass);
                        result.put("Count of usage", usage);
                        return result;
                    }
                };
                return mapper;
            }

            @Override
            public Object[] getParams() {
                return null;
            }
        });
    }

    @RequestMapping(value = "/mostProfitableService")
    @ResponseBody
    public List<Map<String, Object>> generateMostProfitableServiceReport(@RequestParam("period") final String period) {
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT  service_type.name as service_name ,SUM(route.total_price) as profit " +
                        "FROM service_type " +
                        "JOIN taxi_order ON service_type.id=taxi_order.service_type " +
                        "JOIN route ON taxi_order.id=route.order_id " +
                        "WHERE taxi_order.execution_date BETWEEN ? AND ?  " +
                        "GROUP BY service_type.name " +
                        "ORDER BY profit desc";
            }
            @Override
            public RowMapper getRowMapper() {
                RowMapper<Map<String, Object>> mapper = new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        String serviceType = resultSet.getString("service_name");
                        String profit = resultSet.getString("profit");
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("Service name", serviceType);
                        result.put("Profit", profit);
                      Calendar endDate= new GregorianCalendar();
                        Calendar startDate=findDate(period);
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        result.put("Period of time", sdf.format(startDate.getTime())+ " - " + sdf.format(endDate.getTime()));
                        return result;
                    }
                };
                return mapper;
            }
            @Override
            public Object[] getParams() {
                Calendar c = new GregorianCalendar();
                Calendar c2=findDate(period);
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String from=sdf.format(c2.getTime())+" 00:00:0";
                String to =sdf.format(c.getTime())+" 00:00:0";
                return new Object[]{Timestamp.valueOf(from), Timestamp.valueOf(to)};
            }
        });
    }

    private  Calendar findDate(final  String period){
        Calendar c = new GregorianCalendar();
        if (period.equals("MONTH")) {
            c .add(Calendar.MONTH,-1);
        } else if (period.equals("DECADE")) {
            c .add(Calendar.MONTH,-2);
            c.add(Calendar.DAY_OF_MONTH,-14);
        } else {
            c.add(Calendar.DAY_OF_MONTH,-7);
        }
        return c;
    }


    @RequestMapping
    public ModelAndView viewStatistic(Model model, HttpServletRequest request) {
        return new ModelAndView("statistic");
    }
}

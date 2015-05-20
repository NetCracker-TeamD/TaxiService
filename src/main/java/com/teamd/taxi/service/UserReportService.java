package com.teamd.taxi.service;

import com.teamd.taxi.persistence.repository.ReportsRepository;
import com.teamd.taxi.view.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserReportService {
    @Autowired
    private ReportsRepository reportsRepository;

    public List<Map<String, Object>> getServiceProfitability(final long groupId) {
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT " +
                        "EXTRACT(MONTH FROM  route.start_time) as cur_month," +
                        "EXTRACT (YEAR FROM  route.start_time) as cur_year, sum(total_price) as total  " +
                        "FROM route " +
                        "JOIN taxi_order ON taxi_order.id=route.order_id " +
                        "JOIN \"user\" ON \"user\".id=taxi_order.user_id " +
                        "JOIN group_list ON group_list.user_id=\"user\".id " +
                        "WHERE route.start_time IS NOT NULL AND group_list.group_id=? " +
                        "GROUP BY cur_month,cur_year " +
                        "ORDER BY cur_month ";
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
                        double total = resultSet.getDouble("total");
                        DecimalFormatSymbols s = new DecimalFormatSymbols();
                        s.setDecimalSeparator('.');
                        DecimalFormat f = new DecimalFormat("#,##0.00", s);
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("Period of time", monthName + ", " + year);
                        result.put("Profit", f.format(total));
                        return result;
                    }
                };
                return mapper;
            }

            @Override
            public Object[] getParams() {
                return new Object[]{groupId};
            }
        });
    }

    public List<Map<String, Object>> getNewOrders(String startDate, String endDate, final long groupId) {
        final String fromPeriod = startDate + " 00:00:00";
        final String endPeriod = endDate + " 23:59:59";
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT \"user\".last_name as user_last_name,\"user\".first_name as user_first_name, " +
                        "driver.last_name as driver_last_name,driver.first_name as driver_first_name, " +
                        "service_type.name as service_name,route.total_price as profit  " +
                        "FROM \"user\"  " +
                        "JOIN taxi_order ON taxi_order.user_id=\"user\".id  " +
                        "JOIN route ON route.order_id=taxi_order.id  " +
                        "JOIN driver ON driver.id=route.driver_id   " +
                        "JOIN service_type ON service_type.id=taxi_order.service_type   " +
                        "join group_list on group_list.user_id=\"user\".id " +
                        "WHERE taxi_order.execution_date BETWEEN ? AND ? " +
                        "AND  group_id=?";
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
                        double totalPrice = resultSet.getDouble("profit");
                        DecimalFormatSymbols s = new DecimalFormatSymbols();
                        s.setDecimalSeparator('.');
                        DecimalFormat f = new DecimalFormat("#,##0.00", s);
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("User name", userFirstName + " " + userLastName);
                        result.put("Driver name", driverFirstName + " " + driverLastName);
                        result.put("Service name", serviceTypeName);
                        result.put("Profit", f.format(totalPrice));
                        return result;
                    }
                };
            }

            @Override
            public Object[] getParams() {
                return new Object[]{Timestamp.valueOf(fromPeriod), Timestamp.valueOf(endPeriod),groupId};

            }
        });
    }

    public List<Map<String, Object>> getAdditionalOptions(final long groupId) {
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT feature.name as feature_name,COUNT(order_features.feature_id) as usage " +
                        "FROM order_features  " +
                        "JOIN feature ON order_features.feature_id=feature.id  " +
                        "JOIN taxi_order ON taxi_order.id=order_features.order_id " +
                        "JOIN \"user\" ON \"user\".id=taxi_order.user_id " +
                        "JOIN group_list ON group_list.user_id=\"user\".id " +
                        "WHERE group_list.group_id=? " +
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
                return new Object[]{groupId};
            }
        });
    }

    public List<Map<String, Object>> getAdditionalOptionsForUser(final long groupId) {
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT title,max(feature_count)as max_count, user_name,user_surname  " +
                        "FROM " +
                        "(SELECT  feature.name as title,count(order_features.feature_id) AS feature_count, " +
                        "\"user\".first_name as user_name,\"user\".last_name as user_surname  " +
                        "FROM order_features  " +
                        "JOIN feature ON order_features.feature_id=feature.id   " +
                        "JOIN taxi_order ON order_features.order_id=taxi_order.id  " +
                        "JOIN \"user\" ON \"user\".id=taxi_order.user_id  " +
                        "JOIN group_list ON group_list.user_id=\"user\".id " +
                        "WHERE group_list.group_id=? " +
                        "GROUP BY \"user\".first_name,\"user\".last_name, feature.name  " +
                        "ORDER by \"user\".first_name,\"user\".last_name  " +
                        " )as temporary_table  " +
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
                return new Object[]{groupId};
            }
        });
    }

    public List<Map<String, Object>> getPopularCar(final long groupId) {
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT car.model, car_class.class_name,COUNT(taxi_order.id) AS usage  " +
                        "FROM car  " +
                        "JOIN car_class ON car.car_class_id=car_class.id  " +
                        "JOIN driver ON driver.id=car.driver_id  " +
                        "JOIN route ON driver.id=route.driver_id  " +
                        "JOIN taxi_order ON route.order_id=taxi_order.id  " +
                        "JOIN \"user\" ON taxi_order.user_id=\"user\".id " +
                        "JOIN group_list ON group_list.user_id=\"user\".id " +
                        "WHERE group_list.group_id=? " +
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
                return new Object[]{groupId};
            }
        });
    }

    public List<Map<String, Object>> getProfitByPeriod(final String period,final long groupId) {
        return reportsRepository.getReport(new ReportResolver() {
            @Override
            public String getQuery() {
                return "SELECT  service_type.name as service_name ,SUM(route.total_price) as profit " +
                        "FROM service_type  " +
                        "JOIN taxi_order ON service_type.id=taxi_order.service_type  " +
                        "JOIN route ON taxi_order.id=route.order_id  " +
                        "JOIN \"user\" ON \"user\".id=taxi_order.user_id " +
                        "JOIN group_list ON group_list.user_id=\"user\".id " +
                        "WHERE taxi_order.execution_date BETWEEN ? AND ? AND group_list.group_id=?  " +
                        "GROUP BY service_type.name  " +
                        "ORDER BY profit desc";
            }

            @Override
            public RowMapper getRowMapper() {
                RowMapper<Map<String, Object>> mapper = new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        String serviceType = resultSet.getString("service_name");
                        double profit = resultSet.getDouble("profit");
                        DecimalFormatSymbols s = new DecimalFormatSymbols();
                        s.setDecimalSeparator('.');
                        DecimalFormat f = new DecimalFormat("#,##0.00", s);
                        Map<String, Object> result = new TreeMap<String, Object>();
                        result.put("Service name", serviceType);
                        result.put("Profit", f.format(profit));
                        Calendar endDate = new GregorianCalendar();
                        Calendar startDate = findEndDate(period);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        result.put("Period of time", sdf.format(startDate.getTime()) + " - "
                                + sdf.format(endDate.getTime()).toString());
                        return result;
                    }
                };
                return mapper;
            }

            @Override
            public Object[] getParams() {
                Calendar endDate = new GregorianCalendar();
                Calendar startDate = findEndDate(period);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return new Object[]{Timestamp.valueOf(sdf.format(startDate.getTime()) + " 00:00:00"),
                        Timestamp.valueOf(sdf.format(endDate.getTime()) + " 00:00:00"),groupId};
            }
        });
    }

    private Calendar findEndDate(final String period) {
        Calendar startDate = new GregorianCalendar();
        if (period.equals("MONTH")) {
            startDate.add(Calendar.MONTH, -1);
        } else if (period.equals("DECADE")) {
            startDate.add(Calendar.MONTH, -2);
            startDate.add(Calendar.DAY_OF_MONTH, -14);
        } else {
            startDate.add(Calendar.DAY_OF_MONTH, -7);
        }
        return startDate;
    }

    public List getProfitByPeriodList(List<Map<String, Object>> entry) {
        List reportList = new ArrayList();
        for (Map<String, Object> map : entry) {
            reportList.add(new Report((String) map.get("Service name"), (String) map.get("Period of time"),
                    (String) map.get("Profit")));
        }
        return reportList;
    }

    public List getPopularCarList(List<Map<String, Object>> entry) {
        List reportList = new ArrayList();
        for (Map<String, Object> map : entry) {
            reportList.add(new Report((String) map.get("Car model"), (String) map.get("Car class"),
                    (String) map.get("Count of usage")));
        }
        return reportList;
    }

    public List getAdditionalOptionsList(List<Map<String, Object>> entry) {
        List reportList = new ArrayList();
        for (Map<String, Object> map : entry) {
            reportList.add(new Report((String) map.get("Feature name"), (String) map.get("Count of usage")));
        }
        return reportList;
    }

    public List getAdditionalOptionsForUserList(List<Map<String, Object>> entry) {
        List reportList = new ArrayList();
        for (Map<String, Object> map : entry) {
            reportList.add(new Report((String) map.get("User name"), (String) map.get("Feature name"),
                    (String) map.get("Count of usage")));
        }
        return reportList;
    }

    public List getNewOrderList(List<Map<String, Object>> entry) {
        List reportList = new ArrayList();
        for (Map<String, Object> map : entry) {
            reportList.add(new Report((String) map.get("User name"), (String) map.get("Driver name"),
                    (String) map.get("Service name"),(String)map.get("Profit")));
        }
        return reportList;
    }

    public List getServiceProfitabilityList(List<Map<String, Object>> entry) {
        List reportList = new ArrayList();
        for (Map<String, Object> map : entry) {
            reportList.add(new Report((String) map.get("Period of time"), (String) map.get("Profit")));
        }
        return reportList;
    }
}

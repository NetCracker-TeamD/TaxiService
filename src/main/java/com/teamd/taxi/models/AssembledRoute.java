package com.teamd.taxi.models;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AssembledRoute {
    private String source;
    private String destination;
    private Float totalPrice;
    private Float totalDisance;
    private int finishedCars;
    private int totalCars;
    private List<Route> routes;

    public AssembledRoute(String source, String destination, List<Route> routes) {
        this.source = source;
        this.destination = destination;
        this.routes = routes;
        this.totalPrice = 0f;
        this.totalCars = routes.size();

        for (Route route : routes) {
            Float routePrice = route.getTotalPrice();
            if (routePrice == null) {
                totalPrice = null;
                break;
            }
            totalPrice += routePrice;
        }
        for (Route route : routes) {
            RouteStatus status = route.getStatus();
            if (status == RouteStatus.COMPLETED ||
                    status == RouteStatus.REFUSED) {
                finishedCars++;
            }
        }
        if (!routes.isEmpty()) {
            Route sample = routes.get(0);
            if (sample.getOrder().getServiceType().isDestinationRequired()) {
                totalDisance = sample.getDistance();
            }
        }
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route r1, Route r2) {
                //сортировка по водителям, null в конец
                Driver d1 = r1.getDriver();
                Driver d2 = r2.getDriver();
                if (d1 == null) {
                    return 1;
                } else if (d2 == null) {
                    return -1;
                }
                int d1Id = d1.getId();
                int d2Id = d2.getId();
                if (d1Id != d2Id) {
                    return d1Id - d2Id;
                }
                //сортировка по дате начала выполнения, null в конец
                Calendar s1 = r1.getStartTime();
                Calendar s2 = r2.getStartTime();
                if (s1 == null) {
                    return 1;
                } else if (s2 == null) {
                    return -1;
                }
                return s1.compareTo(s2);
            }
        });
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public int getFinishedCars() {
        return finishedCars;
    }

    public int getTotalCars() {
        return totalCars;
    }

    public Float getTotalDistance() {
        return totalDisance;
    }

    @Override
    public String toString() {
        return "AssembledRoute{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", routes=" + routes +
                '}';
    }
}

package com.teamd.taxi.service;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.persistence.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.List;


/**
 * Created by Іван on 02.05.2015.
 */
@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    public List<Route> getFreeRoutsByOrderID(long orderId) {
        List<Route> routes = routeRepository.findFreeRouteByInOrder(orderId);
        return routes;
    }

    public List<Route> getRoutsByOrderAndDriverId(long orderId, int driverId) {
        List<Route> routes = routeRepository.findByOrderAndDriver1(orderId, driverId);
        return routes;
    }


    public Route saveRoute(Route route) {
        return routeRepository.save(route);
    }

    public Route getRouteById(long id) {
        return routeRepository.findOne(id);
    }


    public void updateRouteAssigned(RouteStatus rs, Driver driver, long id){
        routeRepository.updateRouteAssignedById(rs, driver, id);
    }

    public void updateRouteInProgress(RouteStatus rs, Calendar startTime, long id){
        routeRepository.updateRouteInProgressById(rs, startTime, id);
    }

    public void updateRouteCompleted(RouteStatus rs, Calendar complTime, long id){
        routeRepository.updateRouteCompleteById(rs, complTime, id);
    }

    public void setTotalPrice(float totalPrice, long id){
        routeRepository.updateRoutetotalPriceById(totalPrice, id);
    }

    public void updateRouteRefused(RouteStatus rs, Calendar complTime, long id){
        routeRepository.updateRouteRefusedById(rs, complTime, id);
    }
}

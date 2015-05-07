package com.teamd.taxi.service;

import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.persistence.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by Іван on 02.05.2015.
 */
@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    public List<Route> getFreeRouts(long orderId, List<RouteStatus> statusList){
        List<Route> routes = routeRepository.findFreeRouteByInOrder(orderId, statusList);
        return routes;
    }
}

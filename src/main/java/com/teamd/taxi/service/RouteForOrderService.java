package com.teamd.taxi.service;

import com.teamd.taxi.entity.Route;
import com.teamd.taxi.persistence.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Іван on 08.05.2015.
 */
@Service
public class RouteForOrderService {

    @Autowired
    private RouteRepository routeRepository;

    public Route getRouteById( long id){
        return routeRepository.findOne(id);
    }

    public Route update( Route  r ){
        return routeRepository.save(r);
    }
}

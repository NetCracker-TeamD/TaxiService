package com.teamd.taxi.service;

import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.persistence.repository.RouteRepository;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by Іван on 02.05.2015.
 */
@Service
public class TaxiOrderService1 {

    @Autowired
    private TaxiOrderRepository taxiOrderRepository;

    @Autowired
    private RouteRepository routeRepository;


    @Transactional
    public Page<TaxiOrder> getFreeOrder(List<RouteStatus> statusList, Pageable pageable){
        Page<TaxiOrder> orders = taxiOrderRepository.getFreeOrders(statusList, pageable);
        for(TaxiOrder order: orders){
            order.setRoutes(routeRepository.findFreeRouteByInOrder(order.getId(), statusList));
            Hibernate.initialize(order.getRoutes());
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getServiceType());
        }
        return orders;
    }

    @Transactional
    public Page<TaxiOrder> getFilterServiceFreeOrders(List<RouteStatus> statusList, List<Integer> serviceList, Pageable pageable) {
        Page<TaxiOrder> orders = taxiOrderRepository.getFilterFreeOrders(statusList, serviceList, pageable);
        for(TaxiOrder order: orders){
            order.setRoutes(routeRepository.findFreeRouteByInOrder(order.getId(), statusList));
            Hibernate.initialize(order.getRoutes());
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getServiceType());
        }
        return orders;
    }
}

package com.teamd.taxi.service;

import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Anton on 02.05.2015.
 */
@Service
public class TaxiOrderServiceImpl implements TaxiOrderService {

    @Qualifier("taxiOrderRepository")
    @Resource
    TaxiOrderRepository orderRepository;

    Logger logger = Logger.getLogger(TaxiOrderService.class);

    @Override
    @Transactional
    public Page<TaxiOrder> findTaxiOrderByUser(long id, Pageable pageable) {
        if (orderRepository == null) {
            logger.error("orderRepository is null");
        }
        Page<TaxiOrder> to = orderRepository.findByUser_Id(id, pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }

    @Override
    @Transactional
    public Page<TaxiOrder> findTaxiOrderByDriver(int id, Pageable pageable) {
        if (orderRepository == null) {
            logger.error("orderRepository is null");
        }
        Page<TaxiOrder> to = orderRepository.findByDriver_id(id, pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }

    @Override
    @Transactional
    public Page<TaxiOrder> findAll(Pageable pageable) {
        Page<TaxiOrder> to = orderRepository.findAll(pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }
}

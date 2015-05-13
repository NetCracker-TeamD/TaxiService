package com.teamd.taxi.service;

import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
        Page<TaxiOrder> to = orderRepository.findByUserId(id, pageable);
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
        Page<TaxiOrder> to = orderRepository.findByDriverId(id, pageable);
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

    @Override
    @Transactional
    public Page<TaxiOrder> findAll(Specification<TaxiOrder> spec, Pageable pageable) {
        if (spec == null) {
            return findAll(pageable);
        }
        Page<TaxiOrder> to = orderRepository.findAll(spec, pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }

    @Transactional
    public TaxiOrder findOneById(long id) {
        TaxiOrder order = orderRepository.findOne(id);
        Hibernate.initialize(order.getFeatures());
        Hibernate.initialize(order.getRoutes());
        Hibernate.initialize(order.getServiceType());
        return order;
    }

}

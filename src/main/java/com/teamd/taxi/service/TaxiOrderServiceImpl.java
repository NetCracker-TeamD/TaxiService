package com.teamd.taxi.service;

import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
/**
 * Created by Anton on 02.05.2015.
 */
@Service
public class TaxiOrderServiceImpl implements TaxiOrderService{

    @Qualifier("taxiOrderRepository")
    @Resource
    TaxiOrderRepository orderRepository;

    Logger logger = Logger.getLogger(TaxiOrderService.class);
    @Override
    @Transactional
    public Page<TaxiOrder> findTaxiOrderByUser(long id,Pageable pageable) {
        if (orderRepository == null) {
            logger.error("orderRepository is null");
        }
        return orderRepository.findByUser_Id(id, pageable);
    }
    @Override
    @Transactional
    public Page<TaxiOrder> findTaxiOrderByDriver(int id,Pageable pageable){
        if (orderRepository == null) {
            logger.error("orderRepository is null");
        }
        return orderRepository.findByDriver_id(id,pageable);
    }
}

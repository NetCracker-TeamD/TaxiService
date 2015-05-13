package com.teamd.taxi.service;

import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Іван on 13.05.2015.
 */
@Service
public class DemoTaxiOrderService {


    @Autowired
    TaxiOrderRepository orderRepository;


}

package com.teamd.taxi.service;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.persistence.repository.DriverForOrderRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Іван on 08.05.2015.
 */
@Service
public class DriverForOrderService  {

    @Autowired
    DriverForOrderRepository driverForOrderRepository;

    public Driver saveDriver(Driver d){
        return driverForOrderRepository.save(d);
    }

    @Transactional
    public Driver getForId( int id){
        Driver driver =  driverForOrderRepository.findOne(id);
        Hibernate.initialize(driver.getFeatures());
        Hibernate.initialize(driver.getRoutes());
        return driver;
    }
}

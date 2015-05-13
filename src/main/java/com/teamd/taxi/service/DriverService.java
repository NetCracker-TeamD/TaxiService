package com.teamd.taxi.service;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.persistence.repository.DriverRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Іван on 06.05.2015.
 */
@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Transactional
    public Driver getDriver(int id){
        Driver driver = driverRepository.findOne(id);
        Hibernate.initialize(driver.getFeatures());
        Hibernate.initialize(driver.getRoutes());
        return driver;
    }




}

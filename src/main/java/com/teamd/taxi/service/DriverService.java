package com.teamd.taxi.service;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.FeatureType;
import com.teamd.taxi.persistence.repository.DriverRepository;
import com.teamd.taxi.persistence.repository.FeatureRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * Created by Іван on 06.05.2015.
 */
@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Transactional
    public Driver getDriver(int id) {
        Driver driver = driverRepository.findById(id);
        Hibernate.initialize(driver.getFeatures());
        return driver;
    }

    @Transactional
    public Page<Driver> getDrivers(Pageable pageable) {
        Page<Driver> page = driverRepository.findAll(pageable);
        for (Driver driver : page.getContent()) {
            Hibernate.initialize(driver.getFeatures());
        }
        return page;
    }

    public List<Feature> getDriverFeatures() {
        return featureRepository.findAllByFeatureType(FeatureType.DRIVER_FEATURE);
    }




}

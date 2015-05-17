package com.teamd.taxi.service;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.FeatureType;
import com.teamd.taxi.models.admin.UpdateDriverModel;
import com.teamd.taxi.persistence.repository.CarRepository;
import com.teamd.taxi.persistence.repository.DriverRepository;
import com.teamd.taxi.persistence.repository.FeatureRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Іван on 06.05.2015.
 */
@Service
public class DriverService {

    private static final int DRIVER_PASS_LENGTH = 10;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RandomStringGenerator stringGenerator;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private CarRepository carRepository;

    @Transactional
    public Driver getDriver(int id) {
        Driver driver = driverRepository.findOne(id);
        Hibernate.initialize(driver.getFeatures());
        Hibernate.initialize(driver.getRoutes());
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

    @Transactional
    public void removeDriver(int id) {
//        Driver driver = driverRepository.findOne(id);
//        System.out.println(Arrays.toString(driver.getFeatures().toArray()));
//        driver.setFeatures(new ArrayList<Feature>());
//        System.out.println(Arrays.toString(driver.getFeatures().toArray()));
//        driverRepository.saveAndFlush(driver);
//        driver = driverRepository.findOne(id);
//        System.out.println(Arrays.toString(driver.getFeatures().toArray()));
        driverRepository.delete(id);
    }


    public void save(Driver driver) {
        driverRepository.save(driver);
    }

    @Transactional
    public void createDriverAccount(Driver driver) {
        String password = stringGenerator.generateString(DRIVER_PASS_LENGTH);
        //TODO send pass to driver mail address

        driver.setPassword(encoder.encode(password));
        driverRepository.save(driver);
    }

    @Transactional
    public void updateDriverAccount(UpdateDriverModel newDriver) {
        Driver driver = driverRepository.findOne(newDriver.getId());
        driver = newDriver.mergeWith(driver);
        driverRepository.save(driver);
    }


}

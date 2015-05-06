package com.teamd.taxi.service;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.persistence.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Іван on 06.05.2015.
 */
@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public Driver getDriver(int id){
        Driver driver = driverRepository.findById(id);
        return driver;
    }


}

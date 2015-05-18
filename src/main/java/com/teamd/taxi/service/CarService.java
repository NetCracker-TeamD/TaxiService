package com.teamd.taxi.service;

import com.sun.xml.internal.fastinfoset.sax.Features;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.persistence.repository.CarClassRepository;
import com.teamd.taxi.persistence.repository.CarRepository;
import com.teamd.taxi.persistence.repository.DriverRepository;
import com.teamd.taxi.persistence.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 02-May-15.
 *
 * @author Nazar Dub
 */
@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarClassRepository carClassRepository;

    public Page<Car> getCars(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    public List<Feature> getCarFeatures() {
        return featureRepository.findAllByFeatureType(FeatureType.CAR_FEATURE);
    }

    public void removeCar(int id) {
        carRepository.delete(id);
    }

    public List<Driver> getDrivers() {
        return (List<Driver>) driverRepository.findAll();
    }

    public List<Integer> getAllIdDrivers(){
        List<Driver> drivers = this.getDrivers();
        List<Integer> listId = new ArrayList<>();

        for (Driver driver : drivers){
            listId.add(driver.getId());
        }
        return listId;
    }

    public  List<Integer> getAllIdFeatures(){
        List<Feature> features = this.getCarFeatures();
        List<Integer> listId = new ArrayList<>();

        for(Feature feature : features){
            listId.add(feature.getId());
        }
        return listId;
    }

    public Feature getFeature(Integer id){
        return featureRepository.findOne(id);
    }

    public Integer getCountCars(){
        return (int) carRepository.count();
    }

    public Driver getDriver (Integer id){
        return driverRepository.findOne(id);
    }

    public CarClass getCarClass(Integer id){
        return carClassRepository.findOne(id);
    }
    public void saveCar(Car car){
        carRepository.save(car);
    }
}

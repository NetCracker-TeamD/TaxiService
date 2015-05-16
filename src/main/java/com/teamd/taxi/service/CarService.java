package com.teamd.taxi.service;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.FeatureType;
import com.teamd.taxi.persistence.repository.CarRepository;
import com.teamd.taxi.persistence.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<Car> getCars(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    public List<Feature> getCarFeatures() {
        return featureRepository.findAllByFeatureType(FeatureType.CAR_FEATURE);
    }

    public void removeCar(int id) {
        carRepository.delete(id);
    }

    public List<Feature> getFeatureCarByDriverID(int id) {
        return featureRepository.getFeaturesByCarID(id);
    }

}

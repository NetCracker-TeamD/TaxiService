package com.teamd.taxi.service;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.persistence.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Created on 02-May-15.
 *
 * @author Nazar Dub
 */
@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public Page<Car> getCars(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

}

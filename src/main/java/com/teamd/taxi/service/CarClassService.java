package com.teamd.taxi.service;

import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.persistence.repository.CarClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarClassService {

    @Autowired
    private CarClassRepository carClassRepository;
    public List<CarClass> findAll(Sort sort) {
        return carClassRepository.findAll(sort);
    }
    public CarClass findById(int id) {
        return carClassRepository.findOne(id);
    }
    public void save(CarClass carClass){
        carClassRepository.save(carClass);

    }
}

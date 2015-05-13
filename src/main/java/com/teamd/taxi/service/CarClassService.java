package com.teamd.taxi.service;

import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.persistence.repository.CarClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarClassService {

    @Autowired
    private CarClassRepository carClassRepository;

    public List<CarClass> getAllCarClasses(){
        return carClassRepository.findAll();
    }

}

package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

/**
 * Created by Олег on 08.05.2015.
 */
public interface CarClassRepository extends JpaRepository<CarClass, Integer> {

}
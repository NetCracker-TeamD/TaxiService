package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.CarClass;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Олег on 08.05.2015.
 */
public interface CarClassRepository extends JpaRepository<CarClass, Integer> {
}

package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Іван on 06.05.2015.
 */
public interface DriverRepository extends JpaRepository<Driver, Integer> {


    public Driver findById(@Param("i")int id);


}

package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Іван on 08.05.2015.
 */
public interface DriverForOrderRepository extends JpaRepository<Driver, Integer>, CrudRepository<Driver, Integer> {

}

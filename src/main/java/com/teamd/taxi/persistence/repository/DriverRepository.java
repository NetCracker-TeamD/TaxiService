package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Driver;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DriverRepository extends PagingAndSortingRepository<Driver, Integer> {

    Driver findByEmail(String email);

}

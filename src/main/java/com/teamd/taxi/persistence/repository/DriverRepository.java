package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Driver;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DriverRepository extends PagingAndSortingRepository<Driver, Integer> {

    Driver findByEmail(String email);

    public Driver findById(int id);
}

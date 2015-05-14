package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Driver;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DriverRepository extends PagingAndSortingRepository<Driver, Integer> {

    Driver findByEmail(String email);

    @Modifying
    @Query("update Driver d set d.password=?2 where d.id=?1 and d.password=?3")
    void updatePasswordByDriverId(int id, String password, String oldpass);

}

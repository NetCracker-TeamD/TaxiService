package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Олег on 14.05.2015.
 */
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer>, CrudRepository<UserAddress, Integer> {
    @Query("SELECT a FROM UserAddress a WHERE a.user.id = ?1 ORDER BY a.id")
    List<UserAddress> findByUserId(long userId);
}

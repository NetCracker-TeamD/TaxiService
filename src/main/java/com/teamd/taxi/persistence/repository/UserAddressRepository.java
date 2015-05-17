package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Олег on 14.05.2015.
 */
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
    @Query("SELECT a FROM UserAddress a WHERE a.user.id = ?1")
    List<UserAddress> findByUserId(long userId);
}

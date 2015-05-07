package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Іван on 06.05.2015.
 */
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {

    @Query("select service from ServiceType service")
    List<ServiceType>  findAllServ();
}

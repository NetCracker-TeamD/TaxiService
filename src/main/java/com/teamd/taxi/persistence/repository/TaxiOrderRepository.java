package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.entity.Feature;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

public interface TaxiOrderRepository extends JpaRepository<TaxiOrder, Long>, JpaSpecificationExecutor<TaxiOrder>,
        CrudRepository<TaxiOrder, Long>{

    @Query("SELECT t FROM TaxiOrder t WHERE t.customer.id = ?1")
    Page<TaxiOrder> findByUserId(long id, Pageable pageable);

    @Query("select t from TaxiOrder t " +
            "join t.routes r " +
            "where r.driver.id = ?1 " +
            "and r.status in (com.teamd.taxi.entity.RouteStatus.ASSIGNED," +
            "com.teamd.taxi.entity.RouteStatus.IN_PROGRESS ) ")
    List<TaxiOrder> findCurrentOrderByDriverId( int driverId );
}

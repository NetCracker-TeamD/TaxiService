package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.entity.TaxiOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.List;


public interface RouteRepository extends JpaRepository<Route, Long>, CrudRepository<Route, Long> {

    @Query("SELECT r FROM Route r WHERE r.order.id = :id AND r.status in " +
            "(com.teamd.taxi.entity.RouteStatus.QUEUED) ")
    List<Route> findFreeRouteByInOrder(@Param("id") long id);


    @Query("SELECT r FROM Route r WHERE r.order.id = :orderId AND r.driver.id = :driverId")
    List<Route> findByOrderAndDriver(@Param("orderId") long orderId, @Param("driverId") long driverId);


    //потім видалю
    @Query("SELECT r FROM Route r WHERE r.order.id = :orderId AND r.driver.id = :driverId")
    List<Route> findByOrderAndDriver1(@Param("orderId") long orderId, @Param("driverId") int driverId);

    @Modifying
    @Transactional
    @Query("update Route r set r.status = ?1, r.driver = ?2 where r.id = ?3 ")
    void updateRouteAssignedById(RouteStatus rs, Driver driver, long id);

    @Modifying
    @Transactional
    @Query("update Route r set r.status = ?1, r.completionTime = ?2 where r.id = ?3 ")
    void updateRouteCompleteById(RouteStatus rs, Calendar complTime, long id);

    @Modifying
    @Transactional
    @Query("update Route r set r.status = ?1, r.startTime = ?2, r.completionTime = ?2 where r.id = ?3 ")
    void updateRouteRefusedById(RouteStatus rs, Calendar complStartTime, long id);

    @Modifying
    @Transactional
    @Query("update Route r set r.status = ?1, r.startTime = ?2 where r.id = ?3 ")
    void updateRouteInProgressById(RouteStatus rs, Calendar startTime, long id);


    @Modifying
    @Transactional
    @Query("update Route r set r.totalPrice = ?1 where r.id = ?2 ")
    void updateRoutetotalPriceById(Float totalPrice, long id);

}

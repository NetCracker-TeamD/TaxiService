package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.entity.TaxiOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RouteRepository extends JpaRepository<Route, Long>, CrudRepository<Route, Long> {

    @Query("SELECT r FROM Route r WHERE r.order.id = :id AND r.status in " +
            "(com.teamd.taxi.entity.RouteStatus.QUEUED) ")
    List<Route> findFreeRouteByInOrder(@Param("id") long id);


    @Query("SELECT r FROM Route r WHERE r.order.id = :orderId AND r.driver.id = :driverId")
    List<Route> findByOrderAndDriver(@Param("orderId") long orderId, @Param("driverId") long driverId);
}

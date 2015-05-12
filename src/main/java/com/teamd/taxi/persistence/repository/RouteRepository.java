package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.entity.TaxiOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteRepository extends PagingAndSortingRepository<Route, Long> {

    @Query("SELECT r FROM Route r WHERE r.order.id = :id AND r.status in :status")
    List<Route> findFreeRouteByInOrder(@Param("id") long id, @Param("status") List<RouteStatus> statusList);

}

package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.TaxiOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Іван on 03.05.2015.
 */
public interface TaxiOrderRepository  extends PagingAndSortingRepository<TaxiOrder, Long> {

    @Query("select t from TaxiOrder t " +
           "inner join t.routes r "+
           "where r.status in :status")
    Page<TaxiOrder> getFreeOrders(@Param("status")List<RouteStatus> statusList, Pageable pageable);


    @Query("select t from TaxiOrder t " +
            "inner join t.routes r "+
            "where r.status in :status " +
            "and t.serviceType.id in :service")
    Page<TaxiOrder> getFilterFreeOrders(@Param("status")List<RouteStatus> statusList, @Param("service")List<Integer> serviceList, Pageable pageable);

}

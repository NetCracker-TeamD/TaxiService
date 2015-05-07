package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TaxiOrderRepository extends JpaRepository<TaxiOrder, Long>, JpaSpecificationExecutor<TaxiOrder> {

    @Query("select t from TaxiOrder t " +
            "inner join t.routes r " +
            "where r.status in :status")
    Page<TaxiOrder> getFreeOrders(@Param("status") List<RouteStatus> statusList, Pageable pageable);


    @Query("select t from TaxiOrder t " +
            "inner join t.routes r " +
            "where r.status in :status " +
            "and t.serviceType.id in :service")
    Page<TaxiOrder> getFilterFreeOrders(@Param("status") List<RouteStatus> statusList, @Param("service") List<Integer> serviceList, Pageable pageable);

    @Query("SELECT t FROM TaxiOrder t WHERE t.customer.id = ?1")
    Page<TaxiOrder> findByUserId(long id, Pageable pageable);

    @Query("select t from TaxiOrder t " +
            "inner join t.routes r " +
            "where r.driver.id = ?1")
    Page<TaxiOrder> findByDriverId(int id, Pageable pageable);

    Page<TaxiOrder> findAll(Pageable pageable);

    @Query("SELECT DISTINCT t FROM TaxiOrder t" +
            " JOIN t.routes r " +
            "  WHERE t.id NOT IN(" +
            "    SELECT DISTINCT t2.id FROM TaxiOrder  t2" +
            "    JOIN t2.features f1 " +
            "    WHERE f1.id NOT IN (?1)" +
            ")")
    List<TaxiOrder> findBySomething(List<Integer> featureIds, Pageable pageable);

    @Query("SELECT DISTINCT t FROM TaxiOrder t" +
            " JOIN t.routes r " +
            "  WHERE t.id NOT IN(" +
            "    SELECT DISTINCT t2.id FROM TaxiOrder  t2" +
            "    JOIN t2.features f1 " +
            "    WHERE f1.id NOT IN (?1)" +
            ") " +
            "AND r.status NOT IN (com.teamd.taxi.entity.RouteStatus.COMPLETED, com.teamd.taxi.entity.RouteStatus.REFUSED)")
    List<TaxiOrder> findOrderForQueue(List<Integer> featureIds, Pageable pageable);
}

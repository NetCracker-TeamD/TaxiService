package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.TaxiOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TaxiOrderRepository extends PagingAndSortingRepository<TaxiOrder, Long> {
    @Query("SELECT t FROM TaxiOrder t WHERE t.customer.id = ?1")
    Page<TaxiOrder> findByUserId(long id, Pageable pageable);

    @Query("select t from TaxiOrder t " +
            "inner join t.routes r " +
            "where r.driver.id = ?1")
    Page<TaxiOrder> findByDriverId(int id, Pageable pageable);

    Page<TaxiOrder> findAll(Pageable pageable);


    @Query("SELECT t FROM TaxiOrder t " +
            "  WHERE t.id NOT IN(" +
            "    SELECT DISTINCT t2.id FROM TaxiOrder  t2" +
            "    JOIN t2.features f1 " +
            "    WHERE f1.id NOT IN (?1)" +
            ")")
    List<TaxiOrder> findBySomething(List<Integer> featureIds, Pageable pageable);
}

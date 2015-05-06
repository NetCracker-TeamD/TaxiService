package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.TaxiOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Anton on 02.05.2015.
 */

public interface TaxiOrderRepository extends PagingAndSortingRepository<TaxiOrder, Long> {
    @Query("SELECT t FROM TaxiOrder t WHERE t.customer.id=?1")
    Page<TaxiOrder> findByUser_Id(long id, Pageable pageable);

    @Query("select t from TaxiOrder t " +
            "inner join t.routes r " +
            "where r.driver.id=?1")
    Page<TaxiOrder> findByDriver_id(int id, Pageable pageable);
}

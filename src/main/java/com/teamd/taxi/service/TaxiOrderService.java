package com.teamd.taxi.service;

import com.teamd.taxi.entity.TaxiOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Created by Anton on 02.05.2015.
 */
public interface TaxiOrderService {
    Page<TaxiOrder> findTaxiOrderByUser(long id, Pageable pageable);

    Page<TaxiOrder> findTaxiOrderByDriver(int id, Pageable pageable);

    Page<TaxiOrder> findAll(Pageable pageable);

    Page<TaxiOrder> findAll(Specification<TaxiOrder> spec, Pageable pageable);
}

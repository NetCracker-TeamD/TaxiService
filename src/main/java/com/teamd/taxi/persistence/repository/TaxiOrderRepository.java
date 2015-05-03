package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TaxiOrderRepository extends PagingAndSortingRepository<TaxiOrder, Long> {

}

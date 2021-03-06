package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created on 02-May-15.
 *
 * @author Nazar Dub
 */
public interface CarRepository extends PagingAndSortingRepository<Car, Integer> {

    public List<Car> findByDriverId(Integer driverId);
}

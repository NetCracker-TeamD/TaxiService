package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Created on 02-May-15.
 *
 * @author Nazar Dub
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    Page<Car> findAll(Pageable pageable);
}

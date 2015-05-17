package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.TariffByTime;
import com.teamd.taxi.entity.TariffType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TariffByTimeRepository extends JpaRepository<TariffByTime, Integer> {

    List<TariffByTime> findByTariffType(TariffType type);
}

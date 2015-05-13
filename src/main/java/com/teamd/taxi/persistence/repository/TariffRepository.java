package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.TariffByTime;
import com.teamd.taxi.entity.TariffType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Олег on 12.05.2015.
 */
public interface TariffRepository extends JpaRepository<TariffByTime, Integer> {
    List<TariffByTime> findByTariffType(TariffType type);
}

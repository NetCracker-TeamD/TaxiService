package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.TariffByTime;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TariffByTimeRepository extends PagingAndSortingRepository<TariffByTime, Integer> {
}

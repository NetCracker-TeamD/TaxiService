package com.teamd.taxi.service;

import com.teamd.taxi.entity.TariffByTime;
import com.teamd.taxi.entity.TariffType;
import com.teamd.taxi.persistence.repository.TariffByTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class TariffService {

    @Autowired
    private TariffByTimeRepository tariffByTimeRepository;

    public Page<TariffByTime> getTariffs(Pageable pageable) {
        return tariffByTimeRepository.findAll(pageable);
    }

    public void save(TariffByTime tariffByTime) {
        tariffByTimeRepository.save(tariffByTime);
    }

    public TariffByTime findOne(int id){
        return tariffByTimeRepository.findOne(id);
    }

    public void removeTariff(int id) {
        tariffByTimeRepository.delete(id);
    }

    public List<TariffByTime> findTariffsByType(TariffType tariffType) {
        return tariffByTimeRepository.findByTariffType(tariffType);
    }
}

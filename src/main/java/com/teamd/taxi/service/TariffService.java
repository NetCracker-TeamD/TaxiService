package com.teamd.taxi.service;

import com.teamd.taxi.entity.TariffByTime;
import com.teamd.taxi.persistence.repository.TariffByTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TariffService {

    @Autowired
    private TariffByTimeRepository tariffByTimeRepository;

    public Page<TariffByTime> getTariffs(Pageable pageable) {
        return tariffByTimeRepository.findAll(pageable);
    }

    public void save(TariffByTime tariffByTime){
        tariffByTimeRepository.save(tariffByTime);
    }


    public void removeTariff(int id) {
        tariffByTimeRepository.delete(id);
    }

}

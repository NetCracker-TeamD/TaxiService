package com.teamd.taxi.service;

import com.teamd.taxi.entity.Info;
import com.teamd.taxi.persistence.repository.InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Іван on 18.05.2015.
 */
@Service
public class InfoService {

    @Autowired
    InfoRepository infoRepository;

    public Info getIdleFreeTime(String name){
        return infoRepository.findOne(name);
    }
}

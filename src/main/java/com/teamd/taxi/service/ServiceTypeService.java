package com.teamd.taxi.service;

import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.persistence.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Іван on 06.05.2015.
 */
@Service
public class ServiceTypeService {

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    public List<ServiceType> getAllService(){
        return serviceTypeRepository.findAllServ();
    }

}

package com.teamd.taxi.service;

import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.persistence.repository.ServiceTypeRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Іван on 06.05.2015.
 */
@Service
public class ServiceTypeService {

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Transactional
    public List<ServiceType> findAll() {
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();
        for (ServiceType serviceType : serviceTypes) {
            Hibernate.initialize(serviceType.getAllowedCarClasses());
            Hibernate.initialize(serviceType.getAllowedFeatures());
        }
        return serviceTypes;
    }
}

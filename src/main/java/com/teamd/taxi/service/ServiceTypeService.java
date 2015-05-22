package com.teamd.taxi.service;

import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.persistence.repository.ServiceTypeRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional
    public void save(ServiceType serviceType) {
        serviceTypeRepository.save(serviceType);
    }

    @Transactional
    public List<ServiceType> findAll(Sort sort) {
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll(sort);
        for (ServiceType serviceType : serviceTypes) {
            Hibernate.initialize(serviceType.getAllowedCarClasses());
            Hibernate.initialize(serviceType.getAllowedFeatures());
        }
        return serviceTypes;
    }

    @Transactional
    public ServiceType findById(int serviceTypeId) {
        ServiceType serviceType = serviceTypeRepository.findOne(serviceTypeId);
        if (serviceType != null) {
            Hibernate.initialize(serviceType.getAllowedFeatures());
            Hibernate.initialize(serviceType.getAllowedCarClasses());
        }
        return serviceType;
    }

    public List<ServiceType> getAllServices() {
        return serviceTypeRepository.findAll();
    }
}

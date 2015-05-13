package com.teamd.taxi.service;

import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.persistence.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureService {

    @Autowired
    private FeatureRepository featureRepository;

    public List<Feature> getFeatures(){
        return featureRepository.findAll();
    }
}

package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.FeatureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FeatureRepository extends JpaRepository<Feature, Integer> {

    List<Feature> findAllByFeatureType(FeatureType type);

}
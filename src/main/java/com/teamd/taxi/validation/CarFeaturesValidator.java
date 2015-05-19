package com.teamd.taxi.validation;

import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.FeatureType;
import com.teamd.taxi.persistence.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Created by Anatoliy on 19.05.2015.
 */
public class CarFeaturesValidator implements ConstraintValidator<CarFeatures, List<Feature>> {

    @Autowired
    private FeatureRepository featureRepository;

    @Override
    public void initialize(CarFeatures carFeatures) {

    }

    @Override
    public boolean isValid(List<Feature> features, ConstraintValidatorContext constraintValidatorContext) {
        if(features==null)
            return true;
        List<Feature> allFeature = featureRepository.findAllByFeatureType(FeatureType.CAR_FEATURE);
        for (Feature f : features){
            if(!allFeature.contains(f)){
                return false;
            }
        }
        return true;
    }
}

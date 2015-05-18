package com.teamd.taxi.validation;

import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.FeatureType;
import com.teamd.taxi.persistence.repository.DriverRepository;
import com.teamd.taxi.persistence.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Created on 13-May-15.
 *
 * @author Nazar Dub
 */
public class DriverFeaturesValidator implements ConstraintValidator<DriverFeatures, List<Feature>> {

    @Autowired
    private FeatureRepository repository;

    @Override
    public void initialize(DriverFeatures constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<Feature> features, ConstraintValidatorContext context) {
        if (features == null)
            return true;
        List<Feature> allFeatures = repository.findAllByFeatureType(FeatureType.DRIVER_FEATURE);
        for (Feature f : features)
            if (!allFeatures.contains(f))
                return false;
        return true;
    }
}

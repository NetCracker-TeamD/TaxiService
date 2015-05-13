package com.teamd.taxi.models.admin;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Feature;

import java.util.List;

/**
 * Created on 10-May-15.
 *
 * @author Nazar Dub
 */
public class DriverInfoResponseModel extends AdminResponseModel<Driver> {
    private List<Feature> allDriverFeatures;
    private List<Feature> allCarFeatures;

    public DriverInfoResponseModel(List<Feature> allDriverFeatures, List<Feature> allCarFeatures) {
        super();
        this.allDriverFeatures = allDriverFeatures;
        this.allCarFeatures = allCarFeatures;
    }

    public List<Feature> getAllDriverFeatures() {
        return allDriverFeatures;
    }

    public void setAllDriverFeatures(List<Feature> allDriverFeatures) {
        this.allDriverFeatures = allDriverFeatures;
    }

    public List<Feature> getAllCarFeatures() {
        return allCarFeatures;
    }

    public void setAllCarFeatures(List<Feature> allCarFeatures) {
        this.allCarFeatures = allCarFeatures;
    }
}

package com.teamd.taxi.models.admin;

import java.util.List;
import java.util.Map;

/**
 * Created by Anatoliy on 15.05.2015.
 */
public class CarModel {
    private String classId;
    private String category;
    private String driverId;

    private String modelName;
    private String enable;
    private Map<String,String> mapFeatures;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public Map<String, String> getMapFeatures() {
        return mapFeatures;
    }

    public void setMapFeatures(Map<String, String> mapFeatures) {
        this.mapFeatures = mapFeatures;
    }

    @Override
    public String toString() {
        return "CarModel {" +"\n"+
                "\tCarClassID: "+classId+"\n"+
                "\tCarCategory: "+ category +"\n"+
                "\tCarDriverID: "+driverId+"\n"+
                "\tCarModelName: "+modelName+"\n"+
                "\tenable(true/false): "+enable+"\n"+
                "\tCarListMapFeatures: "+mapFeatures+"\n"+
                "}\n";
    }
}


package com.teamd.taxi.models.admin;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.validation.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anatoliy on 19.05.2015.
 */
public class UpdateCarModel {

    @NotNull(message = "Please enter car id")
    @ExistingCarId
    private Integer id;

    @NotBlankOrNull(message = "Please enter car class id")
    @Pattern(regexp = "^[1234]$", message = "Car's class id contains invalid characters")
    private String classId;

    @NotBlankOrNull(message = "Please enter car category")
    @Pattern(regexp = "^[ABCD]$", message = "Car's category contains invalid characters")
    private String category;

    @FreeDriverId
    private Integer driverId;

    @NotBlankOrNull(message = "Please enter car model")
    @CarModelName(regexpError = "[!@%$|*\\\\#/><;^?,=]+")
    private String modelName;


    private Boolean enable;

    @CarFeatures
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Integer> features) {
        LinkedList<Feature> featureList = new LinkedList<>();
        for (Integer id : features)
            featureList.add(new Feature(id));
        this.features = featureList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Car updateCar(Car car){
        if(this.modelName != null) car.setModel(this.modelName);
        if(this.enable != null) car.setEnabled(enable);
        if(this.classId != null) car.setCarClass(new CarClass(Integer.parseInt(this.classId)));
        if(this.category != null) car.setCategory(this.category);
        if(this.features != null) car.setFeatures(this.features);
        if(this.driverId !=null) {
            if(this.driverId.toString().equals("-1")){
                car.setDriver(null);
            }else {
                car.setDriver(new Driver(this.driverId));
            }
        }
        return car;
    }

    @Override
    public String toString() {
        return "UpdateCarModel{" +
                "id=" + id +
                ", classId='" + classId + '\'' +
                ", category='" + category + '\'' +
                ", driverId=" + driverId +
                ", modelName='" + modelName + '\'' +
                ", enable=" + enable +
                ", features=" + features +
                '}';
    }
}

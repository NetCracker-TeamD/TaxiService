/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Олег
 */
@Entity
@Table(name = "car", schema = "public")
public class Car implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "model")
    private String model;

    @Column(name = "car_category")
    private String category;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @JoinTable(name = "car_feature_list",
            joinColumns = {@JoinColumn(name = "car_id", referencedColumnName = "car_id")},
            inverseJoinColumns = {@JoinColumn(name = "feature_id", referencedColumnName = "id")}
    )
    @ManyToMany
    private List<Feature> features;

    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    @OneToOne
    private Driver driver;

    @JoinColumn(name = "car_class_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CarClass carClass;

    public Car() {
    }

    public Car(Integer carId) {
        this.carId = carId;
    }

    public Car(Integer carId, String model, String category, boolean isEnabled) {
        this.carId = carId;
        this.model = model;
        this.category = category;
        this.isEnabled = isEnabled;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String carCategory) {
        this.category = carCategory;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> featureList) {
        this.features = featureList;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClassId) {
        this.carClass = carClassId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carId != null ? carId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Car)) {
            return false;
        }
        Car other = (Car) object;
        if ((this.carId == null && other.carId != null) || (this.carId != null && !this.carId.equals(other.carId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.Car[ carId=" + carId + " ]";
    }

}

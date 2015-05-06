/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * @author Олег
 */
@Entity
@Table(name = "feature", schema = "public")
public class Feature implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private float price;

    @Column(name = "feature_type")
    @Enumerated(EnumType.STRING)
    private FeatureType featureType;

    @JoinTable(name = "order_features",
            joinColumns = {@JoinColumn(name = "feature_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")})
    @ManyToMany
    private List<TaxiOrder> comprisingOrders;

    @JoinTable(name = "service_allowed_features",
            joinColumns = {@JoinColumn(name = "feature_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "service_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ServiceType> comprisingServices;

    @JoinTable(name = "driver_feature_list",
            joinColumns = {@JoinColumn(name = "feature_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "driver_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Driver> drivers;

    @ManyToMany(mappedBy = "features")
    private List<Car> cars;

    public Feature() {
    }

    public Feature(Integer id) {
        this.id = id;
    }

    public Feature(Integer id, String name, float price, FeatureType featureType) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.featureType = featureType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    public List<TaxiOrder> getComprisingOrders() {
        return comprisingOrders;
    }

    public void setComprisingOrders(List<TaxiOrder> taxiOrderList) {
        this.comprisingOrders = taxiOrderList;
    }

    public List<ServiceType> getComprisingServices() {
        return comprisingServices;
    }

    public void setComprisingServices(List<ServiceType> serviceTypeList) {
        this.comprisingServices = serviceTypeList;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> driverList) {
        this.drivers = driverList;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> carList) {
        this.cars = carList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Feature)) {
            return false;
        }
        Feature other = (Feature) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.Feature[ id=" + id + " ]";
    }

}

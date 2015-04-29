/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Олег
 */
@Entity
@Table(name = "car_class", schema = "public")
public class CarClass implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "class_name")
    private String className;

    @Column(name = "price_coef")
    private float priceCoefficient;

    @Column(name = "idle_price_coef")
    private float idlePriceCoefficient;

    @JoinTable(name = "service_allowed_car_classes", joinColumns = {
            @JoinColumn(name = "car_class_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "service_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ServiceType> serviceTypeList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carClass")
    private List<Car> carList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carClass")
    private List<TaxiOrder> taxiOrderList;

    public CarClass() {
    }

    public CarClass(Integer id) {
        this.id = id;
    }

    public CarClass(Integer id, String className, float priceCoefficient, float idlePriceCoefficient) {
        this.id = id;
        this.className = className;
        this.priceCoefficient = priceCoefficient;
        this.idlePriceCoefficient = idlePriceCoefficient;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getPriceCoefficient() {
        return priceCoefficient;
    }

    public void setPriceCoefficient(float priceCoef) {
        this.priceCoefficient = priceCoef;
    }

    public float getIdlePriceCoefficient() {
        return idlePriceCoefficient;
    }

    public void setIdlePriceCoefficient(float idlePriceCoef) {
        this.idlePriceCoefficient = idlePriceCoef;
    }

    public List<ServiceType> getServiceTypeList() {
        return serviceTypeList;
    }

    public void setServiceTypeList(List<ServiceType> serviceTypeList) {
        this.serviceTypeList = serviceTypeList;
    }

    public List<Car> getCarList() {
        return carList;
    }

    public void setCarList(List<Car> carList) {
        this.carList = carList;
    }

    public List<TaxiOrder> getTaxiOrderList() {
        return taxiOrderList;
    }

    public void setTaxiOrderList(List<TaxiOrder> taxiOrderList) {
        this.taxiOrderList = taxiOrderList;
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
        if (!(object instanceof CarClass)) {
            return false;
        }
        CarClass other = (CarClass) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.CarClass[ id=" + id + " ]";
    }

}

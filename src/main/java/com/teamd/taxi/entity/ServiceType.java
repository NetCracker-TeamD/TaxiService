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
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Олег
 */
@Entity
@Table(name = "service_type")
@NamedQueries({
        @NamedQuery(name = "ServiceType.findAll", query = "SELECT s FROM ServiceType s")})
public class ServiceType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "price_by_time")
    private Float priceByTime;

    @Column(name = "price_by_distance")
    private Float priceByDistance;

    @Column(name = "min_price")
    private Float minPrice;

    @ManyToMany(mappedBy = "serviceTypeList")
    private List<CarClass> allowedCarClasses;

    @ManyToMany(mappedBy = "comprisingServices")
    private List<Feature> allowedFeatures;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceType")
    private List<TaxiOrder> taxiOrderList;

    public ServiceType() {
    }

    public ServiceType(Integer id) {
        this.id = id;
    }

    public ServiceType(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public Float getPriceByTime() {
        return priceByTime;
    }

    public void setPriceByTime(Float priceByTime) {
        this.priceByTime = priceByTime;
    }

    public Float getPriceByDistance() {
        return priceByDistance;
    }

    public void setPriceByDistance(Float priceByDistance) {
        this.priceByDistance = priceByDistance;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public List<CarClass> getAllowedCarClasses() {
        return allowedCarClasses;
    }

    public void setAllowedCarClasses(List<CarClass> carClassList) {
        this.allowedCarClasses = carClassList;
    }

    public List<Feature> getAllowedFeatures() {
        return allowedFeatures;
    }

    public void setAllowedFeatures(List<Feature> featureList) {
        this.allowedFeatures = featureList;
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
        if (!(object instanceof ServiceType)) {
            return false;
        }
        ServiceType other = (ServiceType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.ServiceType[ id=" + id + " ]";
    }

}

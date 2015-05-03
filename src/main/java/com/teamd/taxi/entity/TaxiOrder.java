/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 * @author Олег
 */
@Entity
@Table(name = "taxi_order", schema = "public")
public class TaxiOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "registration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar registrationDate;

    @Column(name = "execution_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar executionDate;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "music_style")
    private String musicStyle;

    @Column(name = "driver_sex")
    @Enumerated(EnumType.STRING)
    private Sex driverSex;

    @Column(name = "comment")
    private String comment;

    @Column(name = "secret_view_key")
    private String secretViewKey;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "comprisingOrders")
    private List<Feature> features;

    @OneToMany( fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
    private List<Route> routes;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User customer;

    @JoinColumn(name = "service_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ServiceType serviceType;

    @JoinColumn(name = "car_class_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CarClass carClass;

    public TaxiOrder() {
    }

    public TaxiOrder(Long id) {
        this.id = id;
    }

    public TaxiOrder(Long id, Calendar registrationDate, Calendar executionDate, PaymentType paymentType) {
        this.id = id;
        this.registrationDate = registrationDate;
        this.executionDate = executionDate;
        this.paymentType = paymentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Calendar registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Calendar getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Calendar executionDate) {
        this.executionDate = executionDate;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getMusicStyle() {
        return musicStyle;
    }

    public void setMusicStyle(String musicStyle) {
        this.musicStyle = musicStyle;
    }

    public Sex getDriverSex() {
        return driverSex;
    }

    public void setDriverSex(Sex driverSex) {
        this.driverSex = driverSex;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> featureList) {
        this.features = featureList;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routeList) {
        this.routes = routeList;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User userId) {
        this.customer = userId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClassId) {
        this.carClass = carClassId;
    }

    public String getSecretViewKey() {
        return this.secretViewKey;
    }

    public void setSecretViewKey(String key) {
        this.secretViewKey = key;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TaxiOrder)) {
            return false;
        }
        TaxiOrder other = (TaxiOrder) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.TaxiOrder[ id=" + id + " ]";
    }

}

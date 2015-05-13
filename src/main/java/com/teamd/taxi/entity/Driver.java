/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadingAction;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Олег
 */
@Entity
@Table(name = "driver", schema = "public")
public class Driver implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "driver_password")
    private String password;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "license")
    private String license;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "at_work")
    private boolean atWork;

    //Changed by Dub 12.05.12 to make easier crud operations on driver entity
    //@ManyToMany(mappedBy = "drivers")
    @JoinTable(name = "driver_feature_list",
            inverseJoinColumns = {@JoinColumn(name = "feature_id", referencedColumnName = "id")},
            joinColumns = {@JoinColumn(name = "driver_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Feature> features;

    @OneToOne(mappedBy = "driver")
    private Car car;

    @OneToMany(mappedBy = "driver")
    private List<Route> routes;

    public Driver() {
    }

    public Driver(Integer id) {
        this.id = id;
    }

    public Driver(Integer id, String email, String firstName,
                  String lastName, String password, Sex sex,
                  String phoneNumber, String license,
                  boolean isEnabled, boolean atWork) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.license = license;
        this.isEnabled = isEnabled;
        this.atWork = atWork;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String driverPassword) {
        this.password = driverPassword;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isAtWork() {
        return atWork;
    }

    public void setAtWork(boolean atWork) {
        this.atWork = atWork;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> featureList) {
        this.features = featureList;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routeList) {
        this.routes = routeList;
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
        if (!(object instanceof Driver)) {
            return false;
        }
        Driver other = (Driver) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.Driver[ id=" + id + " ]";
    }

}

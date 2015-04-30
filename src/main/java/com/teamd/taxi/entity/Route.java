/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Олег
 */
@Entity
@Table(name = "route", schema = "public")
public class Route implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    private String status;

    @Column(name = "source_address")
    private String sourceAddress;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "distance")
    private Float distance;

    @Column(name = "total_price")
    private Float totalPrice;

    @Column(name = "is_late")
    private Boolean isLate;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "completion_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completionTime;

    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TaxiOrder order;

    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    @ManyToOne
    private Driver driver;

    public Route() {
    }

    public Route(Long id) {
        this.id = id;
    }

    public Route(Long id, String status, String sourceAddress, String destinationAddress) {
        this.id = id;
        this.status = status;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;
    }

    public TaxiOrder getOrder() {
        return order;
    }

    public void setOrder(TaxiOrder orderId) {
        this.order = orderId;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driverId) {
        this.driver = driverId;
    }

    public Boolean getIsLate() {
        return isLate;
    }

    public void setIsLate(Boolean customerIsLate) {
        this.isLate = customerIsLate;
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
        if (!(object instanceof Route)) {
            return false;
        }
        Route other = (Route) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.Route[ id=" + id + " ]";
    }

}

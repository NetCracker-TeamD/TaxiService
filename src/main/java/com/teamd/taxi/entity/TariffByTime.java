/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.*;

/**
 * @author Олег
 */
@Entity
@Table(name = "tariff_by_time", schema = "public")
public class TariffByTime implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "from")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar from;

    @Column(name = "to")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar to;

    @Column(name = "price")
    private float price;

    @Column(name = "tariff_type")
    @Enumerated(EnumType.STRING)
    private TariffType tariffType;

    public TariffByTime() {
    }

    public TariffByTime(Integer id) {
        this.id = id;
    }

    public TariffByTime(Integer id, Calendar from, Calendar to, float price, TariffType tariffType) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.price = price;
        this.tariffType = tariffType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Calendar getFrom() {
        return from;
    }

    public void setFrom(Calendar from) {
        this.from = from;
    }

    public Calendar getTo() {
        return to;
    }

    public void setTo(Calendar to) {
        this.to = to;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public TariffType getTariffType() {
        return tariffType;
    }

    public void setTariffType(TariffType tariffType) {
        this.tariffType = tariffType;
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
        if (!(object instanceof TariffByTime)) {
            return false;
        }
        TariffByTime other = (TariffByTime) object;
        if (this.id != null && !this.id.equals(other.id)) {
            return false;
        } else if (((this.id == null) && (other.id != null))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.TariffByTime[ id=" + id + " ]";
    }

}

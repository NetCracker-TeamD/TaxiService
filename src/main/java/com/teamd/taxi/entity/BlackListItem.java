package com.teamd.taxi.entity;

import javax.persistence.*;

@Entity
@Table(name = "black_list_item", schema = "public")
public class BlackListItem {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payed")
    private Boolean payed;

    @Column(name = "multiplier")
    private Integer multiplier;

    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private User user;

    @JoinColumn(name = "taxi_order_to_pay", referencedColumnName = "id", unique = true)
    @OneToOne
    private TaxiOrder taxiOrderToPay;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TaxiOrder getTaxiOrderToPay() {
        return taxiOrderToPay;
    }

    public void setTaxiOrderToPay(TaxiOrder taxiOrderToPay) {
        this.taxiOrderToPay = taxiOrderToPay;
    }

    public Boolean isPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public Integer getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Integer multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BlackListItem)) {
            return false;
        }
        BlackListItem other = (BlackListItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.BlackListItem[ id=" + id + " ]";
    }

}

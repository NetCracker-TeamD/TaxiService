/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * @author Олег
 */
@Entity
@Table(name = "user", schema = "public")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Column(name = "is_confirmed")
    private Boolean confirmed;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<BlackListItem> blackListItems;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<GroupList> groups;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserAddress> addresses;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<TaxiOrder> orders;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String firstName, String lastName, UserRole userRole, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<BlackListItem> getBlackListItems() {
        return blackListItems;
    }

    public void setBlackListItems(List<BlackListItem> blackListItems) {
        this.blackListItems = blackListItems;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public Boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean isConfirmed) {
        this.confirmed = isConfirmed;
    }

    public List<GroupList> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupList> groups) {
        this.groups = groups;
    }

    public List<UserAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserAddress> userAdressList) {
        this.addresses = userAdressList;
    }

    public List<TaxiOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<TaxiOrder> orders) {
        this.orders = orders;
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
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.User[ id=" + id + " ]";
    }

}

package com.teamd.taxi.models;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Олег on 19.05.2015.
 */
public class AddressForm {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    public AddressForm() {

    }

    public AddressForm(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

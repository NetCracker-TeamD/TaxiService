package com.teamd.taxi.models.admin;

import com.teamd.taxi.validation.CarModelName;
import com.teamd.taxi.validation.GroupProcentBorders;
import org.hibernate.validator.constraints.NotBlank;


import javax.validation.constraints.Pattern;

/**
 * Created by Anatoliy on 22.05.2015.
 */
public class CreateGroupModel {

    @NotBlank(message = "Please enter group name")
    @CarModelName(regexpError = "[!@%$|*\\\\#/><;^?,=]+")
    private String name;

    @NotBlank(message = "Please enter group discount")
    @Pattern(regexp = "^[+]?\\d+(\\.)?(\\d+)?$", message = "Group's discount field contains invalid characters")
    @GroupProcentBorders
    private String discount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "CreateGroupModel{" +
                "name='" + name + '\'' +
                ", discount='" + discount + '\'' +
                '}';
    }
}

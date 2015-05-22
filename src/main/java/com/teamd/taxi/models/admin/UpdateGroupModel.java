package com.teamd.taxi.models.admin;

import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.validation.CarModelName;
import com.teamd.taxi.validation.ExistingGroupId;
import com.teamd.taxi.validation.NotBlankOrNull;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by Anatoliy on 22.05.2015.
 */
public class UpdateGroupModel {

    @NotNull(message = "Please enter group id")
    @ExistingGroupId
    private Integer id;

    @NotBlankOrNull(message = "Please enter group name")
    @CarModelName(regexpError = "[!@%$|*\\\\#/><;^?,=]+")
    private String name;

    @NotBlankOrNull(message = "Please enter group discount")
    @Pattern(regexp = "^[-+]?\\d+(\\.)?(\\d+)?$", message = "Group's discount field contains invalid characters")
    private String discount;

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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public UserGroup changeGroup(UserGroup userGroup){
        if(name!=null) userGroup.setName(name);
        if(discount!=null) userGroup.setDiscount(Float.parseFloat(discount));
        return userGroup;
    }

    @Override
    public String toString() {
        return "UpdateGroupModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", discount='" + discount + '\'' +
                '}';
    }
}

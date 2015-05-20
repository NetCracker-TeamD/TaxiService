package com.teamd.taxi.models.admin;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.Sex;
import com.teamd.taxi.validation.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 12-May-15.
 *
 * @author Nazar Dub
 */
public class CreateDriverModel {

    @NotBlank(message = "Please enter driver first name.")
    private String firstName;

    @NotBlank(message = "Please enter driver last name.")
    private String lastName;

    @NotBlank(message = "Please enter driver email.")
    @Email(regexp = "^(.+)@(.+)$", message = "Email address contains illegal characters")
    @UniqueDriverEmail
    private String email;

    @NotBlank(message = "Please enter driver phone number.")
    @Phone
    private String phoneNumber;

    @NotBlank(message = "Please enter driver license serial.")
    @License
    private String license;

    private boolean isEnabled;

    private boolean atWork;

    @NotNull
    private Sex sex;

    @NotNull
    @DriverFeatures
    private List<Feature> features;

    @FreeCarId
    private Integer carId;


    public CreateDriverModel() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Integer> features) {
        LinkedList<Feature> featureList = new LinkedList<>();
        for (Integer id : features)
            featureList.add(new Feature(id));
        this.features = featureList;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Driver toDriver() {
        Driver d = new Driver();
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setEmail(email);
        d.setPhoneNumber(phoneNumber);
        d.setSex(sex);
        d.setEnabled(isEnabled);
        d.setAtWork(atWork);
        d.setLicense(license);
        d.setFeatures(features);
        if (carId != null)
            d.setCar(new Car(carId));
        return d;
    }

    @Override
    public String toString() {
        return "DriverModel{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", license='" + license + '\'' +
                ", isEnabled=" + isEnabled +
                ", atWork=" + atWork +
                ", sex=" + sex +
                ", features=" + features +
                ", carId=" + carId +
                '}';
    }
}

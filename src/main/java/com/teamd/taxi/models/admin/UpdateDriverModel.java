package com.teamd.taxi.models.admin;

import com.teamd.taxi.entity.Car;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.Sex;
import com.teamd.taxi.validation.*;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 16-May-15.
 *
 * @author Nazar Dub
 */
public class UpdateDriverModel {


    @NotNull(message = "Please enter driver id.")
    @ExistingDriverId
    private Integer id;

    @NotBlankOrNull(message = "Please enter driver first name.")
    private String firstName;

    @NotBlankOrNull(message = "Please enter driver last name.")
    private String lastName;

    @NotBlankOrNull(message = "Please enter driver email.")
    @Email(regexp = "^(.+)@(.+)$", message = "Email address contains illegal characters")
    @UniqueDriverEmail
    private String email;

    @NotBlankOrNull(message = "Please enter driver phone number.")
    @Phone
    private String phoneNumber;

    @NotBlankOrNull(message = "Please enter driver license serial.")
    @License
    private String license;

    private Boolean isEnabled;

    private Boolean atWork;

    private Sex sex;

    @DriverFeatures
    private List<Feature> features;

    @FreeCarId
    private Integer carId;

    private boolean carChange;

    public UpdateDriverModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean isCarChange() {
        return carChange;
    }

    public void setCarChange(boolean carChange) {
        this.carChange = carChange;
    }

    public Driver toDriver() {
        Driver d = new Driver();
        d.setId(this.id);
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setEmail(email);
        d.setPhoneNumber(phoneNumber);
        d.setSex(sex);
        d.setEnabled(isEnabled);
        d.setAtWork(atWork);
        d.setLicense(license);
        d.setFeatures(features);
        return d;
    }

    /**
     * <p>This method add not null fields from model to specified in param driver</p>
     *
     * @param driver driver to merge
     * @return merged driver
     */
    public Driver mergeWith(Driver driver) {
        if (firstName != null) driver.setFirstName(firstName);
        if (lastName != null) driver.setLastName(lastName);
        if (email != null) driver.setEmail(email);
        if (phoneNumber != null) driver.setPhoneNumber(phoneNumber);
        if (sex != null) driver.setSex(sex);
        if (isEnabled != null) driver.setEnabled(isEnabled);
        if (atWork != null) driver.setAtWork(atWork);
        if (license != null) driver.setLicense(license);
        if (features != null) driver.setFeatures(features);
        if (carChange && carId != null) {
            driver.setCar(new Car(carId));
        } else if (carChange) {
            driver.setCar(null);
        }
        return driver;
    }

    @Override
    public String toString() {
        return "UpdateDriverModel{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
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

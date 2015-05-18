package com.teamd.taxi.models;

import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.PaymentType;
import com.teamd.taxi.entity.ServiceType;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Олег on 13.05.2015.
 */
public class TaxiOrderForm {
    private ServiceType serviceType;
    private List<String> source;
    private List<String> intermediate;
    private List<String> destination;
    private List<Integer> carsAmount;
    private CarClass carClass;
    private String driverSex;
    private List<Feature> features;
    private Calendar execDate;
    private PaymentType paymentType;

    public Calendar getExecDate() {
        return execDate;
    }

    public void setExecDate(Calendar execDate) {
        this.execDate = execDate;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public List<String> getSource() {
        return source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public List<String> getIntermediate() {
        return intermediate;
    }

    public void setIntermediate(List<String> intermediate) {
        this.intermediate = intermediate;
    }

    public List<String> getDestination() {
        return destination;
    }

    public void setDestination(List<String> destination) {
        this.destination = destination;
    }

    public List<Integer> getCarsAmount() {
        return carsAmount;
    }

    public void setCarsAmount(List<Integer> carsAmount) {
        this.carsAmount = carsAmount;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    public String getDriverSex() {
        return driverSex;
    }

    public void setDriverSex(String driverSex) {
        this.driverSex = driverSex;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return "TaxiOrderForm{" +
                "serviceType=" + serviceType +
                ", source=" + source +
                ", intermediate=" + intermediate +
                ", destination=" + destination +
                ", carsAmount=" + carsAmount +
                ", carClass=" + carClass +
                ", driverSex='" + driverSex + '\'' +
                ", features=" + features +
                ", execDate=" + execDate +
                ", paymentType=" + paymentType +
                '}';
    }
}

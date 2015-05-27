package com.teamd.taxi.exception;

/**
 * Created by Іван on 23.05.2015.
 */
public class DriverHasActiveOrderException extends Exception {

    public DriverHasActiveOrderException(String issue, long orderId, long driverId) {
        super(issue+" [ Driver with ID: "+driverId+" has order " + orderId + " ]");
    }

    public DriverHasActiveOrderException() { }
}

package com.teamd.taxi.exception;

/**
 * Created by Олег on 13.05.2015.
 */
public class PropertyNotFoundException extends Exception {
    public PropertyNotFoundException(String propertyName) {
        super(propertyName);
    }
}

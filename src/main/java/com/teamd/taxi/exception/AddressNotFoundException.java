package com.teamd.taxi.exception;

/**
 * Created by Олег on 14.05.2015.
 */
public class AddressNotFoundException extends Exception {
    public AddressNotFoundException(String address) {
        super(address);
    }
}

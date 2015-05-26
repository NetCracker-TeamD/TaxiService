package com.teamd.taxi.exception;

/**
 * Created by Іван on 23.05.2015.
 */
public class TaxiOrderNotExist extends Exception {
    public TaxiOrderNotExist(String issue, long id) {
        super(issue+ " on [" + id + "]");
    }

    public TaxiOrderNotExist() {}
}

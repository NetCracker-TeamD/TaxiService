package com.teamd.taxi.exception;

/**
 * Created by Олег on 21.05.2015.
 */
public class OrderNotUnderUpdatingException extends Exception {
    public OrderNotUnderUpdatingException(long orderId) {
        super(orderId + "");
    }
}

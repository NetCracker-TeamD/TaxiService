package com.teamd.taxi.exception;

/**
 * Created by Олег on 21.05.2015.
 */
public class OrderUpdatingException extends Exception {
    public OrderUpdatingException(String cause, long orderId) {
        super(cause + "on [" + orderId + "]");
    }
}

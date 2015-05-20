package com.teamd.taxi.exception;

/**
 * Created by Олег on 21.05.2015.
 */
public class OrderNotUpdatableException extends Exception {
    public OrderNotUpdatableException(long id) {
        super(Long.toString(id));
    }
}

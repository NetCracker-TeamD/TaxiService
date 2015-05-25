package com.teamd.taxi.exception;

/**
 * Created by Олег on 24.05.2015.
 */
public class SecretKeyMismatchException extends Exception {
    public SecretKeyMismatchException() {
        super("Received secret key mismatch with order secret key");
    }
}

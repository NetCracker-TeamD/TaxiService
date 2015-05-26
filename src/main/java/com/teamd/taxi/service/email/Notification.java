package com.teamd.taxi.service.email;

/**
 * Standart notifications
 *
 * @author Ivaniv Ivan, Oleg Choniy
 */
public enum Notification {

    REGISTRATION("You successfully registered in the system. Follow this link to confirm yourself <a href=\"%1$s\">%1$s</a>"),
    NEW_ORDER("Your order with track number #%d available at <a href=%2$s>%2$s</a>"),
    ASSIGNED("Your order is accepted. Expect a taxi at %1$tm %1$te,%1$tY %1$tT at %2$s"),
    IN_PROGRESS("Your order with source: %s is in progress"),
    COMPLETED("Your order with source: %1$s is completed at %2$tT "),
    REFUSED("Sorry, but you didn't manage to show up at %s in time. Your order was rejected."),
    DRIVER_REGISTRATION("You successfully registered in the system. Your password is: %s");

    private String pattern;

    Notification(String pattern) {
        this.pattern = pattern;
    }

    public String getBody(Object... params) {
        return String.format(pattern, params);
    }
}

package com.teamd.taxi.service.email;

/**
 * Standart notifications
 *
 * @author Ivaniv Ivan, Oleg Choniy
 */
public enum Notification {

    REGISTRATION("You successfully registered in the system. Follow this link to confirm yourself <a href=\"%1$s\">%1$s</a>"),
    NEW_ORDER("Your order with track number #%1$d available at <a href=%2$s>%2$s</a>"),
    ASSIGNED("Your order is accepted. Expect a taxi at %1$tm %1$te,%1$tY %1$tT at %2$s  <a href=\"%3$s\">%3$s</a>"),
    IN_PROGRESS("Your order with source: %1$s is in progress  <a href=\"%2$s\">%2$s</a>"),
    COMPLETED("Your order with source: %1$s is completed at %2$tT  <a href=\"%3$s\">%3$s</a>"),
    REFUSED("Sorry, but you didn't manage to show up at %1$s in time. Your order was rejected.  <a href=\"%2$s\">%2$s</a>"),
    DRIVER_REGISTRATION("You successfully registered in the system. Your password is: %1$s  <a href=\"%2$s\">%2$s</a>");

    private String pattern;

    Notification(String pattern) {
        this.pattern = pattern;
    }

    public String getBody(Object... params) {
        return String.format(pattern, params);
    }
}

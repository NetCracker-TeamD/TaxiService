package com.teamd.taxi.service.email;

/**
 * Standart notifications
 *
 * @author Ivaniv Ivan, Oleg Choniy
 */
public enum Notification {

    REGISTRATION("You successfully registered in the system. Follow this link to confirm yourself <a href=\"%1$s\">%1$s</a>");

    private String pattern;

    Notification(String pattern) {
        this.pattern = pattern;
    }

    public String getBody(Object... params) {
        return String.format(pattern, params);
    }
}

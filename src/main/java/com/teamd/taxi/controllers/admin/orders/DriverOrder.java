package com.teamd.taxi.controllers.admin.orders;

/**
 * Created on 07-May-15.
 *
 * @author Nazar Dub
 */
public enum DriverOrder {
    LAST_NAME("lastName");
    private String order;

    DriverOrder(String order) {
        this.order = order;
    }

    public String getOrder() {
        return order;
    }
}

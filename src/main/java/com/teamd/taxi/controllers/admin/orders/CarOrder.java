package com.teamd.taxi.controllers.admin.orders;

/**
 * Created on 04-May-15.
 *
 * @author Nazar Dub
 */
public enum CarOrder {
    MODEL("model"),
    DRIVER("driver.lastName"),
    CLASS("carClass.className"),
    CATEGORY("category");
    private String order;

    CarOrder(String order) {
        this.order = order;
    }

    public String getOrder() {
        return order;
    }
}

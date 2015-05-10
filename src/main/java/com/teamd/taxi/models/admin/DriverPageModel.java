package com.teamd.taxi.models.admin;

import com.teamd.taxi.controllers.admin.orders.CarOrder;
import com.teamd.taxi.controllers.admin.orders.DriverOrder;
import com.teamd.taxi.entity.Driver;

import javax.validation.constraints.Min;

/**
 * Created on 07-May-15.
 *
 * @author Nazar Dub
 */
public class DriverPageModel {
    private static final Integer DEFAULT_PAGE = 0;
    private static final DriverOrder DEFAULT_ORDER = DriverOrder.LAST_NAME;

    @Min(0)
    private Integer page = DEFAULT_PAGE;

    private DriverOrder order = DEFAULT_ORDER;

    public DriverPageModel() {
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getOrder() {
        return order.getOrder();
    }

    public void setOrder(String order) {
        this.order = DriverOrder.valueOf(order.toUpperCase());
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "page=" + page +
                ", order='" + order + '\'' +
                '}';
    }
}

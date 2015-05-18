package com.teamd.taxi.models.admin;

import com.teamd.taxi.controllers.admin.orders.CarOrder;

import javax.validation.constraints.Min;

/**
 * Created on 04-May-15.
 *
 * @author Nazar Dub
 */
public class CarsPageModel {
    private static final Integer DEFAULT_PAGE = 0;
    private static final CarOrder DEFAULT_ORDER = CarOrder.MODEL;

    @Min(0)
    private Integer page = DEFAULT_PAGE;

    private CarOrder order = DEFAULT_ORDER;

    public CarsPageModel() {
    }

    public CarOrder getCleanOrder() {
        return order;
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
        this.order = CarOrder.valueOf(order.toUpperCase());
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "page=" + page +
                ", order='" + order + '\'' +
                '}';
    }
}

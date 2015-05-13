package com.teamd.taxi.models.admin;


import com.teamd.taxi.controllers.admin.orders.TariffOrder;

import javax.validation.constraints.Min;

public class TariffsByTimeModel {
    private static final Integer DEFAULT_PAGE = 0;
    private static final TariffOrder DEFAULT_ORDER = TariffOrder.ID;

    @Min(0)
    private Integer page = DEFAULT_PAGE;

    private TariffOrder order = DEFAULT_ORDER;

    public TariffsByTimeModel() {
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getOrder() {
        return order.toString().toLowerCase();
    }

//    public void setOrder(CarOrder order) {
//        this.order = order;
//    }

    public void setOrder(String order) {
        this.order = TariffOrder.valueOf(order.toUpperCase());
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "page=" + page +
                ", order='" + order + '\'' +
                '}';
    }
}

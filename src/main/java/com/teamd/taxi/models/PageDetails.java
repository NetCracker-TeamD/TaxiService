package com.teamd.taxi.models;

import org.springframework.data.domain.Page;

public class PageDetails {

    private long totalElements;
    private int totalPage;
    private int elementsNumber;
    private int pageNumber;

    public PageDetails(Page<?> page) {
        totalElements = page.getTotalElements();
        totalPage = page.getTotalPages();
        elementsNumber = page.getNumberOfElements();
        pageNumber = page.getNumber();
    }
}
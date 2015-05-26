package com.teamd.taxi.models;

/**
 * Created by Олег on 25.05.2015.
 */
public class Price {
    private Float originalPrice;
    private Float priceWithDiscount;
    private Float penaltyPrice;
    private Float featurePrice;

    public Price() {
    }

    public Float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Float getPriceWithDiscount() {
        return priceWithDiscount;
    }

    public void setPriceWithDiscount(Float priceWithDiscount) {
        this.priceWithDiscount = priceWithDiscount;
    }

    public Float getPenaltyPrice() {
        return penaltyPrice;
    }

    public void setPenaltyPrice(Float penaltyPrice) {
        this.penaltyPrice = penaltyPrice;
    }

    public Float getFeaturePrice() {
        return featurePrice;
    }

    public void setFeaturePrice(Float featurePrice) {
        this.featurePrice = featurePrice;
    }
}

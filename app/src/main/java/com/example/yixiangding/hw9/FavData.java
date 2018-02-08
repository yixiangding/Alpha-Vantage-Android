package com.example.yixiangding.hw9;

/**
 * Data item in fav list
 */

public class FavData {
    private String symbol;
    private String price;
    private String change;
    private String changePercentage;
    private long addedTime;

    public FavData(String symbol, String price, String change, String changePercentage, long addedTime) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.changePercentage = changePercentage;
        this.addedTime = addedTime;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public String getChange() {
        return change;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPrice() {
        return price;
    }

    public String getChangePercentage() {
        return changePercentage;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public void setChangePercentage(String changePercentage) {
        this.changePercentage = changePercentage;
    }
}

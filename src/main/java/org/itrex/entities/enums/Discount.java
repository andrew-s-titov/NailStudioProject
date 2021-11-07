package org.itrex.entities.enums;

public enum Discount {
    ZERO("0%"),
    TWO("2%"),
    THREE("3%"),
    FIVE("5%"),
    TEN("10%");

    public final String percentString;

    Discount(String percentString) {
        this.percentString = percentString;
    }
}
package org.itrex.entities.enums;

public enum Discount {
    ZERO(1.00),
    TWO(0.98),
    THREE(0.97),
    FIVE(0.95),
    TEN(0.90);

    public final double totalPricePercentValue;

    Discount(double totalPricePercentValue) {
        this.totalPricePercentValue = totalPricePercentValue;
    }
}
package org.itrex.entities.enums;

public enum RecordTime {
    NINE("09:00"),
    THIRTEEN("13:00"),
    SEVENTEEN("17:00");

    public final String digitsText;

    RecordTime(String digitsText) {
        this.digitsText = digitsText;
    }
}
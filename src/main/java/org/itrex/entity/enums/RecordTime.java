package org.itrex.entity.enums;

public enum RecordTime {
    NINE("09:00"),
    THIRTEEN("13:00"),
    SEVENTEEN("17:00");

    public final String digitsText;

    RecordTime(String digitsText) {
        this.digitsText = digitsText;
    }

    public static RecordTime fromString(String text) {
        for (RecordTime recordTime : RecordTime.values()) {
            if (recordTime.digitsText.equalsIgnoreCase(text)) {
                return recordTime;
            }
        }
        return null;
    }
}
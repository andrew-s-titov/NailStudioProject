package org.itrex.exception;

import org.itrex.entity.enums.RecordTime;

import java.time.LocalDate;

public class BookingUnavailableException extends Exception {
    public BookingUnavailableException(LocalDate date, RecordTime time) {
        super(String.format("Oooops... Someone has already booked %s on %s for chosen professional. Please, try again.",
                time.digitsText, date));
    }
}
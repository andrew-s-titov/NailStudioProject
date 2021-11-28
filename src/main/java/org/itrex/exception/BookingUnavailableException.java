package org.itrex.exception;

import org.itrex.entity.enums.RecordTime;

import java.time.LocalDate;

public class BookingUnavailableException extends Exception {
    public BookingUnavailableException(LocalDate date, RecordTime time) {
        super("Oooops... Someone has already booked " +
                time.digitsText +
                " on " +
                date + " for chosen professional. Please, try again.");
    }
}
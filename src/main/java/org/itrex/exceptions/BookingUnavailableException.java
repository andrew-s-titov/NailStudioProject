package org.itrex.exceptions;

public class BookingUnavailableException extends Exception {
    public BookingUnavailableException() {
        super("Oooops... Someone has already booked this time for chosen professional. Please, refresh page and try again.");
    }
}
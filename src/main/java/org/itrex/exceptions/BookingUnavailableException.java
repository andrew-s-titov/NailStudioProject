package org.itrex.exceptions;

public class BookingUnavailableException extends Exception {
    public BookingUnavailableException() {
        super("This time has already been booked!");
    }
}
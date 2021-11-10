package org.itrex.exceptions;

public class DatabaseEntryNotFoundException extends RuntimeException {
    public DatabaseEntryNotFoundException(String message) {
        super(message);
    }
}
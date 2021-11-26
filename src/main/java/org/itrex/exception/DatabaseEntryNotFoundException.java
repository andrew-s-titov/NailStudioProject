package org.itrex.exception;

public class DatabaseEntryNotFoundException extends RuntimeException {
    public DatabaseEntryNotFoundException(String message) {
        super(message);
    }
}
package org.itrex.exception;

public class DatabaseEntryNotFoundException extends Exception {
    public DatabaseEntryNotFoundException(String message) {
        super(message);
    }
}
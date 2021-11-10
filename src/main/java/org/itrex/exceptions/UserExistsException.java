package org.itrex.exceptions;

public class UserExistsException extends Exception {
    public UserExistsException() {
        super("User with the same phone number already exists!");
    }
}
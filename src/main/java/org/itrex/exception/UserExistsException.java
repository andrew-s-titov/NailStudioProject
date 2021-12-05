package org.itrex.exception;

public class UserExistsException extends Exception {
    public UserExistsException(String phone) {
        super(String.format("User with the same login (phone number %s) already exists!", phone));
    }
}
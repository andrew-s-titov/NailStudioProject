package org.itrex.exception;

public class UserExistsException extends Exception {
    public UserExistsException(String phone) {
        super("User with the same login (phone number " +
                phone +
                ") already exists!");
    }
}
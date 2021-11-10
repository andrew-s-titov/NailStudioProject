package org.itrex.exceptions;

public class DeletingUserWithActiveRecordsException extends Exception {
    public DeletingUserWithActiveRecordsException() {
        super("Cannot delete User with active records!");
    }
}
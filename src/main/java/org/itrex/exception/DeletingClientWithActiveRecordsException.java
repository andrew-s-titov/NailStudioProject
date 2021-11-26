package org.itrex.exception;

public class DeletingClientWithActiveRecordsException extends Exception {
    public DeletingClientWithActiveRecordsException() {
        super("Cannot delete client with active records!");
    }
}
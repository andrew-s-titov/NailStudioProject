package org.itrex.exception;

public class DeletingClientWithActiveRecordsException extends Exception {
    public DeletingClientWithActiveRecordsException(Long userId) {
        super("Cannot delete client with id " +
                userId +
                ": client has active (future) records!");
    }
}
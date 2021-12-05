package org.itrex.exception;

public class DeletingClientWithActiveRecordsException extends Exception {

    public DeletingClientWithActiveRecordsException(Long userId) {
        super(String.format("Cannot delete client with id %s: client has active (future) records!", userId));
    }
}
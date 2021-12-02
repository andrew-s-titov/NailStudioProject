package org.itrex.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.itrex.exception.DatabaseEntryNotFoundException;

import java.util.Optional;

@Slf4j
public abstract class BaseService {
    protected <T> T entityExists(Optional<T> optional, String entityName, Object searchParam, String paramName)
            throws DatabaseEntryNotFoundException {

        if (optional.isEmpty()) {
            String message = String.format("%s with %s %s wasn't found.", entityName, paramName, searchParam);
            log.debug("DatabaseEntryNotFoundException was thrown: {}", message);
            throw new DatabaseEntryNotFoundException(message);
        }
        return optional.get();
    }
}
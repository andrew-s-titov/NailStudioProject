package org.itrex.controller;

import org.itrex.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler(LoginFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String loginFailed(LoginFailedException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(RoleManagementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String roleManagementFailed(RoleManagementException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DeletingClientWithActiveRecordsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String deleteClientWithActiveRecords(DeletingClientWithActiveRecordsException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler({UserExistsException.class, BookingUnavailableException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    String userExists(Exception ex) {
        return ex.getMessage();
    }
}
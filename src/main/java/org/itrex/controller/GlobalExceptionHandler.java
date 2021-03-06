package org.itrex.controller;

import org.itrex.dto.ValidationErrorDTO;
import org.itrex.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> login(BadCredentialsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RoleManagementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> roleManagementFailed(RoleManagementException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeletingClientWithActiveRecordsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> deleteClientWithActiveRecords(DeletingClientWithActiveRecordsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UserExistsException.class, BookingUnavailableException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> userExists(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DatabaseEntryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> entryNotFound(DatabaseEntryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorDTO> validation(BindException ex) {
        String exMessage = String.format("Validation errors: %s, including field errors - %s",
                ex.getErrorCount(), ex.getFieldErrorCount());
        Map<String, String> invalidFields = new HashMap<>();

        if (ex.hasFieldErrors()) {
            ex.getFieldErrors()
                    .forEach(f -> invalidFields.put(String.format("field %s (%s)", f.getField(), f.getRejectedValue()),
                            f.getDefaultMessage()));
        }

        ValidationErrorDTO validationErrorDTO = ValidationErrorDTO.builder()
                .message(exMessage)
                .validationErrors(invalidFields)
                .build();
        return new ResponseEntity<>(validationErrorDTO, HttpStatus.BAD_REQUEST);
    }
}
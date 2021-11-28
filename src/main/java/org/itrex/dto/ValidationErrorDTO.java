package org.itrex.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ValidationErrorDTO {
    private String message;
    private Map<String, String> validationErrors;
}

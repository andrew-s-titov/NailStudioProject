package org.itrex.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserUpdateDTO {

    private Long userId;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String lastName;

    @Email(message = "Should match e-mail address format")
    private String email;
}
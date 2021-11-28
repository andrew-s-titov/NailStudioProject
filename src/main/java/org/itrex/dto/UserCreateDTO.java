package org.itrex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDTO {
    @NotEmpty
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotEmpty
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String lastName;

    @NotEmpty
    @Size(min = 9, max = 13, message = "Phone number must be between 9 and 13 digits")
    private String phone;

    @Email(message = "Should match e-mail address format")
    private String email;

    @NotEmpty
    @Size(min = 4, message = "Password should be at least 4-character long")
    private String password;
}
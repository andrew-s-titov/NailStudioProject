package org.itrex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDTO {

    @NotEmpty(message = "Field must not be empty")
    @Size(min = 9, max = 13, message = "Phone number must be between 9 and 13 digits")
    private String phone;

    @NotEmpty(message = "Field must not be empty")
    @Size(min = 4, message = "Password should be at least 4-character long")
    private String password;
}
package org.itrex.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
}

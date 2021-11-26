package org.itrex.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
}

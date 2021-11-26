package org.itrex.dto;

import lombok.Builder;
import lombok.Data;

import org.itrex.entity.enums.Discount;

@Data
@Builder
public class UserCreateDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private Discount discount;
}
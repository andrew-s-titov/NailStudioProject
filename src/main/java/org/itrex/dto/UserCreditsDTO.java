package org.itrex.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreditsDTO {
    private Long userId;
    private String phone;
    private byte[] password;
}
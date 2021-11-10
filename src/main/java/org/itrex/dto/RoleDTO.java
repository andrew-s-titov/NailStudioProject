package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entities.enums.RoleType;

@Data
@Builder
public class RoleDTO {
    private long roleId;
    private RoleType roletype;
}

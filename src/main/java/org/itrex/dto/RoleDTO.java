package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entity.enums.RoleType;

@Data
@Builder
public class RoleDTO {
    private Integer roleId;
    private RoleType roleType;
}
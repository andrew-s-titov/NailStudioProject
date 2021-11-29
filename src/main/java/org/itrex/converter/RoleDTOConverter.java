package org.itrex.converter;

import org.itrex.dto.RoleDTO;
import org.itrex.entity.Role;
import org.springframework.stereotype.Component;

@Component
public interface RoleDTOConverter {
    Role fromRoleDTO(RoleDTO roleDTO);
    RoleDTO toRoleDTO(Role roleEntity);
}
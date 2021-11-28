package org.itrex.converter;

import org.itrex.dto.RoleDTO;
import org.itrex.entity.Role;

public interface RoleDTOConverter {
    Role fromRoleDTO(RoleDTO roleDTO);
}
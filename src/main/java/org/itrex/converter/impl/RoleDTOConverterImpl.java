package org.itrex.converter.impl;

import org.itrex.converter.RoleDTOConverter;
import org.itrex.dto.RoleDTO;
import org.itrex.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleDTOConverterImpl implements RoleDTOConverter {
    @Override
    public Role fromRoleDTO(RoleDTO roleDTO) {
        return Role.builder()
                .roleId(roleDTO.getRoleId())
                .roleType(roleDTO.getRoleType())
                .build();
    }

    @Override
    public RoleDTO toRoleDTO(Role roleEntity) {
        return RoleDTO.builder()
                .roleId(roleEntity.getRoleId())
                .roleType(roleEntity.getRoletype())
                .build();
    }
}
package org.itrex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.converter.RoleDTOConverter;
import org.itrex.dto.RoleDTO;
import org.itrex.entity.Role;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.repository.RoleRepo;
import org.itrex.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;
    private final RoleDTOConverter roleDTOConverter;

    @Override
    public List<RoleDTO> getRoles() {
        return roleRepo.getRoles().stream()
                .map(roleDTOConverter::toRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO getRoleByName(String roleName) throws DatabaseEntryNotFoundException {
        Optional<Role> role = roleRepo.getRoleByName(roleName);
        if (role.isEmpty()) {
            String message = String.format("Role \"%s\" wasn't found", roleName);
            log.debug(message + "DatabaseEntryNotFoundException was thrown while executing getRoleByName method.");
            throw new DatabaseEntryNotFoundException(message);
        }
        return roleDTOConverter.toRoleDTO(role.get());
    }

    @Override
    public RoleDTO getRoleById(Integer roleId) throws DatabaseEntryNotFoundException {
        Optional<Role> role = roleRepo.getRoleById(roleId);
        if (role.isEmpty()) {
            String message = String.format("Role with id %s wasn't found", roleId);
            log.debug(message + "DatabaseEntryNotFoundException was thrown while executing getRoleById method.");
            throw new DatabaseEntryNotFoundException(message);
        }
        return roleDTOConverter.toRoleDTO(role.get());
    }
}
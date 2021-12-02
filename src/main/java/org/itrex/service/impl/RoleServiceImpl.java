package org.itrex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.converter.RoleDTOConverter;
import org.itrex.dto.RoleDTO;
import org.itrex.entity.Role;
import org.itrex.entity.enums.RoleType;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.repository.data.RoleRepository;
import org.itrex.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepo;
    private final RoleDTOConverter roleDTOConverter;

    @Override
    public List<RoleDTO> getRoles() {
        return roleRepo.findAll().stream()
                .map(roleDTOConverter::toRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO getByName(String roleName) throws DatabaseEntryNotFoundException {
        Optional<Role> role = Optional.empty();
        if (Arrays.stream(RoleType.values()).anyMatch(r -> r.name().equals(roleName.toUpperCase()))) {
            RoleType roleType = RoleType.valueOf(roleName.toUpperCase());
            role = roleRepo.findByRoleType(roleType);
        }
        if (role.isEmpty()) {
            String message = String.format("Role \"%s\" wasn't found", roleName);
            log.debug(message + "DatabaseEntryNotFoundException was thrown while executing getRoleByName method.");
            throw new DatabaseEntryNotFoundException(message);
        }
        return roleDTOConverter.toRoleDTO(role.get());
    }

    @Override
    public RoleDTO getRoleById(Integer roleId) throws DatabaseEntryNotFoundException {
        Optional<Role> role = roleRepo.findById(roleId);
        if (role.isEmpty()) {
            String message = String.format("Role with id %s wasn't found", roleId);
            log.debug(message + "DatabaseEntryNotFoundException was thrown while executing getRoleById method.");
            throw new DatabaseEntryNotFoundException(message);
        }
        return roleDTOConverter.toRoleDTO(role.get());
    }
}
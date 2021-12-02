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
public class RoleServiceImpl extends BaseService implements RoleService {
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
        Optional<Role> optionalRole = Optional.empty();
        if (Arrays.stream(RoleType.values()).anyMatch(r -> r.name().equals(roleName.toUpperCase()))) {
            RoleType roleType = RoleType.valueOf(roleName.toUpperCase());
            optionalRole = roleRepo.findByRoleType(roleType);
        }
        Role role = entityExists(optionalRole, "Role", roleName, "name");
        return roleDTOConverter.toRoleDTO(role);
    }

    @Override
    public RoleDTO getRoleById(Integer roleId) throws DatabaseEntryNotFoundException {
        Role role = entityExists(roleRepo.findById(roleId), "Role", roleId, "id");
        return roleDTOConverter.toRoleDTO(role);
    }
}
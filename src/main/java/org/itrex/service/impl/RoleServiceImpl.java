package org.itrex.service.impl;

import lombok.RequiredArgsConstructor;
import org.itrex.dto.RoleDTO;
import org.itrex.entity.Role;
import org.itrex.repository.RoleRepo;
import org.itrex.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;

    @Override
    public List<RoleDTO> getRoles() {
        return roleRepo.getRoles().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO getRoleByName(String roleName) {
        return entityToDTO(roleRepo.getRoleByName(roleName));
    }

    @Override
    public RoleDTO getRoleById(Integer roleId) {
        return entityToDTO(roleRepo.getRoleById(roleId));
    }

    private RoleDTO entityToDTO(Role roleEntity) {
        return RoleDTO.builder()
                .roleId(roleEntity.getRoleId())
                .roletype(roleEntity.getRoletype())
                .build();
    }
}

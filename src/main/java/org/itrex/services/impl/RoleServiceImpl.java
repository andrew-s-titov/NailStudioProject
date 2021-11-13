package org.itrex.services.impl;

import lombok.RequiredArgsConstructor;
import org.itrex.dto.RoleDTO;
import org.itrex.entities.Role;
import org.itrex.repositories.RoleRepo;
import org.itrex.services.RoleService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
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
    public RoleDTO getRoleByName(String name) {
        return entityToDTO(roleRepo.getRoleByName(name));
    }

    @Override
    public RoleDTO getRoleById(Serializable id) {
        return entityToDTO(roleRepo.getRoleById(id));
    }

    private RoleDTO entityToDTO(Role roleEntity) {
        return RoleDTO.builder()
                .roleId(roleEntity.getRoleId())
                .roletype(roleEntity.getRoletype())
                .build();
    }
}
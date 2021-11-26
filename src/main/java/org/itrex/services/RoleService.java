package org.itrex.services;

import org.itrex.dto.RoleDTO;

import java.util.List;

public interface RoleService {
    List<RoleDTO> getRoles();

    RoleDTO getRoleByName(String name);

    RoleDTO getRoleById(Integer id);
}

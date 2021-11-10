package org.itrex.services;

import org.itrex.dto.RoleDTO;

import java.io.Serializable;
import java.util.List;

public interface RoleService {
    List<RoleDTO> getRoles();

    RoleDTO getRoleByName(String name);

    RoleDTO getRoleById(Serializable id);
}

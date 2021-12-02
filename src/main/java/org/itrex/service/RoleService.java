package org.itrex.service;

import org.itrex.dto.RoleDTO;
import org.itrex.exception.DatabaseEntryNotFoundException;

import java.util.List;

public interface RoleService {
    List<RoleDTO> getRoles();

    RoleDTO getByName(String name) throws DatabaseEntryNotFoundException;

    RoleDTO getRoleById(Integer id) throws DatabaseEntryNotFoundException;
}
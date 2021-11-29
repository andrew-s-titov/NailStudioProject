package org.itrex.repository;

import org.itrex.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepo {
    List<Role> getRoles();

    Optional<Role> getRoleByName(String name);

    Optional<Role> getRoleById(Integer id);
}
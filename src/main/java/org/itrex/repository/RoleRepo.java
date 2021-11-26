package org.itrex.repository;

import org.itrex.entity.Role;

import java.util.List;

public interface RoleRepo {
    List<Role> getRoles();

    Role getRoleByName(String name);

    Role getRoleById(Integer id);
}
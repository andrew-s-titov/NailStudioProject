package org.itrex.repositories;

import org.itrex.entities.Role;

import java.io.Serializable;
import java.util.List;

public interface RoleRepo {
    List<Role> getRoles();

    Role getRoleByName(String name);

    Role getRoleById(Serializable id);
}
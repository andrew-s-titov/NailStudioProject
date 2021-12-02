package org.itrex.repository;

import org.itrex.entity.Role;
import org.itrex.entity.enums.RoleType;

import java.util.List;
import java.util.Optional;

@Deprecated
public interface RoleRepo {
    List<Role> findAll();

    Optional<Role> findByRoleType(RoleType roleType);

    Optional<Role> findById(Integer roleId);
}
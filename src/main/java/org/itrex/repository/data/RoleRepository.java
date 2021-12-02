package org.itrex.repository.data;

import org.itrex.entity.Role;
import org.itrex.entity.enums.RoleType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByRoleType(RoleType roleType);

    List<Role> findAll();
}
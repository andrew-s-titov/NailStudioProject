package org.itrex.repository.impl;

import org.itrex.TestBaseHibernateRepository;
import org.itrex.entity.Role;
import org.itrex.entity.enums.RoleType;
import org.itrex.repository.RoleRepo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Using deprecated repository class")
public class HibernateRoleRepoTest extends TestBaseHibernateRepository {
    @Autowired
    private RoleRepo repo;

    @Test
    @DisplayName("findAll - should return a list of Roles")
    public void findAll() {
        // given & when
        List<Role> roles = repo.findAll();

        // then
        assertEquals(3, roles.size());
    }

    @Test
    @DisplayName("findByRoleType with valid data - should return single Role of given type")
    public void findByRoleType() {
        // given & when
        RoleType roleType1 = RoleType.ADMIN;
        RoleType roleType2 = RoleType.STAFF;
        Role role1 = repo.findByRoleType(roleType1).get();
        Role role2 = repo.findByRoleType(roleType2).get();

        // then
        assertEquals(RoleType.ADMIN, role1.getRoleType());
        assertEquals(RoleType.ADMIN, role2.getRoleType());
        assertEquals(1, role1.getRoleId());
    }


    @Test
    @DisplayName("findById with valid data - should return a Role with given id")
    public void findById1() {
        // given
        Integer roleId = 1;

        // when
        Role role = repo.findById(roleId).get();

        // then
        assertEquals(1, role.getRoleId());
        assertEquals(RoleType.ADMIN, role.getRoleType());
    }

    @Test
    @DisplayName("findById with invalid data - should return empty Optional")
    public void findById2() {
        // given
        Integer roleId = 150;

        // when
        Optional<Role> roleById = repo.findById(roleId);

        // then
        assertTrue(roleById.isEmpty());
    }
}

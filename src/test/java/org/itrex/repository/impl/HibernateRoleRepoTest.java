package org.itrex.repository.impl;

import org.itrex.TestBaseHibernateRepository;
import org.itrex.entity.Role;
import org.itrex.entity.enums.RoleType;
import org.itrex.repository.RoleRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateRoleRepoTest extends TestBaseHibernateRepository {
    @Autowired
    private RoleRepo repo;

    @Test
    @DisplayName("getRoles - should return a list of Roles")
    public void getRoles() {
        // given & when
        List<Role> roles = repo.getRoles();

        // then
        assertEquals(3, roles.size());
    }

    @Test
    @DisplayName("getRoleByName with valid data - should return single Role of given type")
    public void getRoleByName1() {
        // given & when
        String roleName1 = "admin";
        String roleName2 = "AdmIN";
        Role role1 = repo.getRoleByName(roleName1).get();
        Role role2 = repo.getRoleByName(roleName2).get();

        // then
        assertEquals(RoleType.ADMIN, role1.getRoletype());
        assertEquals(RoleType.ADMIN, role2.getRoletype());
        assertEquals(1, role1.getRoleId());
    }

    @Test
    @DisplayName("getRoleByName with invalid data - should return empty Optional")
    public void getRoleByName2() {
        // given
        String roleName = "noRole"; // no such role

        // when
        Optional<Role> roleByName = repo.getRoleByName(roleName);

        // then
        assertTrue(roleByName.isEmpty());
    }

    @Test
    @DisplayName("getRoleById with valid data - should return a Role with given id")
    public void getRoleById1() {
        // given
        Integer roleId = 1;

        // when
        Role role = repo.getRoleById(roleId).get();

        // then
        assertEquals(1, role.getRoleId());
        assertEquals(RoleType.ADMIN, role.getRoletype());
    }

    @Test
    @DisplayName("getRoleById with invalid data - should return empty Optional")
    public void getRoleById2() {
        // given
        Integer roleId = 150;

        // when
        Optional<Role> roleById = repo.getRoleById(roleId);

        // then
        assertTrue(roleById.isEmpty());
    }
}

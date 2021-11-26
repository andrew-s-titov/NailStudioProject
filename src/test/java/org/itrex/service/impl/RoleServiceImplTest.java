package org.itrex.service.impl;

import org.itrex.TestBaseHibernate;
import org.itrex.dto.RoleDTO;
import org.itrex.entity.enums.RoleType;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoleServiceImplTest extends TestBaseHibernate {
    @Autowired
    private RoleService service;

    @Test
    @DisplayName("getRoles - should return a list of RoleDTO")
    public void getRoles() {
        // given & when
        List<RoleDTO> roles = service.getRoles();

        // then
        assertEquals(3, roles.size());
        assertEquals(RoleType.ADMIN, roles.get(0).getRoletype());
        assertEquals(RoleType.STAFF, roles.get(1).getRoletype());
        assertEquals(RoleType.CLIENT, roles.get(2).getRoletype());
    }

    @Test
    @DisplayName("getRoleByName with valid data - should return single RoleDTO with given name")
    public void getRoleByName1() {
        // given & when
        String roleName1 = "admin";
        String roleName2 = "AdmIN";
        RoleDTO roleDTO1 = service.getRoleByName(roleName1);
        RoleDTO roleDTO2 = service.getRoleByName(roleName2);

        // then
        assertEquals(RoleType.ADMIN, roleDTO1.getRoletype());
        assertEquals(RoleType.ADMIN, roleDTO2.getRoletype());
        assertEquals(1, roleDTO1.getRoleId());
    }

    @Test
    @DisplayName("getRoleByName with invalid data - should throw DatabaseEntryNotFoundException")
    public void getRoleByName2() {
        // given
        String roleName = "noRole"; // no such role

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getRoleByName(roleName));
    }

    @Test
    @DisplayName("getRoleById with valid data - should return a RoleDTO with given id")
    public void getRoleById1() {
        // given
        Integer roleId = 1;

        // when
        RoleDTO roleDTO = service.getRoleById(roleId);

        // then
        assertEquals(1, roleDTO.getRoleId());
        assertEquals(RoleType.ADMIN, roleDTO.getRoletype());
    }

    @Test
    @DisplayName("getRoleById with invalid data - should throw DatabaseEntryNotFoundException")
    public void getRoleById2() {
        // given
        Integer roleId = 150;

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getRoleById(roleId));
    }
}
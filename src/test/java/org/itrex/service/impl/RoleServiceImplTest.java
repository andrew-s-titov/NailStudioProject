package org.itrex.service.impl;

import org.itrex.dto.RoleDTO;
import org.itrex.entity.Role;
import org.itrex.entity.enums.RoleType;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.repository.data.RoleRepository;
import org.itrex.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RoleServiceImplTest {
    @MockBean
    private RoleRepository roleRepo;
    @Autowired
    private RoleService service;

    @Test
    @DisplayName("getRoles - should return a list of RoleDTO with data equal to Roles")
    public void getRoles() {
        // given
        List<Role> roles = Arrays.asList(new Role(1, RoleType.ADMIN),
                new Role(2, RoleType.STAFF),
                new Role(3, RoleType.CLIENT));
        when(roleRepo.findAll()).thenReturn(roles);

        // & when
        List<RoleDTO> returnedRoles = service.getRoles();

        // then
        assertEquals(3, returnedRoles.size());
        assertEquals(RoleType.ADMIN, returnedRoles.get(0).getRoleType());
        assertEquals(RoleType.STAFF, returnedRoles.get(1).getRoleType());
        assertEquals(RoleType.CLIENT, returnedRoles.get(2).getRoleType());

        verify(roleRepo).findAll();
    }

    @Test
    @DisplayName("getByName with valid data - should return single RoleDTO with given name")
    public void getByName1() throws DatabaseEntryNotFoundException {
        // given
        String roleName1 = "admin";
        String roleName2 = "aDmIn";
        Role admin = new Role(1, RoleType.ADMIN);
        when(roleRepo.findByRoleType(RoleType.ADMIN)).thenReturn(Optional.of(admin));

        // when
        RoleDTO roleDTO1 = service.getByName(roleName1);
        RoleDTO roleDTO2 = service.getByName(roleName2);

        // then
        assertEquals(RoleType.ADMIN, roleDTO1.getRoleType());
        assertEquals(RoleType.ADMIN, roleDTO2.getRoleType());
        assertEquals(1, roleDTO1.getRoleId());

        verify(roleRepo, times(2)).findByRoleType(RoleType.ADMIN);
    }

    @Test
    @DisplayName("getByName with invalid data - should throw DatabaseEntryNotFoundException")
    public void getByName2() {
        // given
        String roleName1 = "noRole"; // no such role

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getByName("noRole"));
    }

    @Test
    @DisplayName("getRoleById with valid data - should return a RoleDTO with given id")
    public void getRoleById1() throws DatabaseEntryNotFoundException {
        // given
        Integer roleId = 1;
        when(roleRepo.findById(roleId)).thenReturn(Optional.of(new Role(1, RoleType.ADMIN)));

        // when
        RoleDTO roleDTO = service.getRoleById(roleId);

        // then
        assertEquals(1, roleDTO.getRoleId());
        assertEquals(RoleType.ADMIN, roleDTO.getRoleType());

        verify(roleRepo).findById(roleId);
    }

    @Test
    @DisplayName("getRoleById with invalid data - should throw DatabaseEntryNotFoundException")
    public void getRoleById2() {
        // given
        Integer roleId = 150;
        when(roleRepo.findById(roleId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getRoleById(roleId));

        verify(roleRepo).findById(roleId);
    }
}

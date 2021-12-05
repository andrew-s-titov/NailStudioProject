package org.itrex.controller;

import org.itrex.dto.RoleDTO;
import org.itrex.entity.enums.RoleType;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("+375295055055")
@ActiveProfiles("test")
public class RoleControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoleService roleService;

    private final RoleDTO role1 = RoleDTO.builder().roleId(1).roleType(RoleType.ADMIN).build();
    private final RoleDTO role2 = RoleDTO.builder().roleId(2).roleType(RoleType.STAFF).build();
    private final RoleDTO role3 = RoleDTO.builder().roleId(3).roleType(RoleType.CLIENT).build();

    @Test
    @DisplayName("getAllRoles - should return json data about all roles")
    public void getAllRoles() throws Exception {
        // given
        when(roleService.getRoles()).thenReturn(Arrays.asList(role1, role2, role3));

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(Arrays.asList(role1, role2, role3))))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @DisplayName("getByName with valid data - should return json with data equal to Role with given name")
    public void getByName1() throws Exception {
        // given
        String roleName = "aDmIn";
        when(roleService.getByName(roleName)).thenReturn(role1);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/roles/get/name/" + roleName))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(role1)))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @DisplayName("getByName with invalid data - should handle DatabaseEntryNotFoundException")
    public void getByName2() throws Exception {
        // given
        String roleName = "nonExistingRole";
        when(roleService.getByName(roleName)).thenThrow(DatabaseEntryNotFoundException.class);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/roles/get/name/" + roleName))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof DatabaseEntryNotFoundException);
    }

    @Test
    @DisplayName("getRoleById with valid data - should return json with data equal to Role with given id ")
    public void getRoleById1() throws Exception {
        // given
        Integer roleId = role1.getRoleId();
        when(roleService.getRoleById(roleId)).thenReturn(role1);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/roles/get/id/" + roleId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(role1)))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @DisplayName("getRoleById with invalid data - should handle DatabaseEntryNotFoundException")
    public void getRoleById2() throws Exception {
        // given
        Integer roleId = 100500;
        when(roleService.getRoleById(roleId)).thenThrow(DatabaseEntryNotFoundException.class);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/roles/get/id/" + roleId))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof DatabaseEntryNotFoundException);
    }
}
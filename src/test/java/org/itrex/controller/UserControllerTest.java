package org.itrex.controller;

import org.itrex.converter.UserDTOConverter;
import org.itrex.dto.RoleDTO;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entity.User;
import org.itrex.entity.enums.Discount;
import org.itrex.entity.enums.RoleType;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;
import org.itrex.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private UserDTOConverter converter;

    private final User user1 = User.builder()
            .userId(1L)
            .firstName("Thomas")
            .lastName("Anderson")
            .email("wakeUpNeo@yahoo.com")
            .phone("(312)555-0690")
            .password("theOne")
            .build();

    private final User user2 = User.builder()
            .userId(2L)
            .firstName("Morpheus")
            .lastName("NoName")
            .email("Nebuchadnezzar@yahoo.com")
            .phone("(313)010-0690")
            .password("redPill")
            .build();

    @Test
    @WithUserDetails("+375293000000")
    @DisplayName("getAll - should return json with data about all users")
    public void getAll() throws Exception {
        // given
        PageRequest defaultPageRequest = PageRequest.of(0, 20, Sort.by("lastName", "firstName"));
        PageRequest somePageRequest = PageRequest.of(1, 1, Sort.by("lastName", "firstName"));
        List<UserResponseDTO> users = Arrays.asList(converter.toUserResponseDTO(user1), converter.toUserResponseDTO(user2));
        when(userService.getAll(defaultPageRequest)).thenReturn(users);
        when(userService.getAll(somePageRequest)).thenReturn(Collections.singletonList(converter.toUserResponseDTO(user1)));

        // when & then
        mockMvc.perform(get("/users/get/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(users)));

        mockMvc.perform(get("/users/get/all")
                .param("page", String.valueOf(1))
                .param("size", String.valueOf(1))
                .param("sort", "lastName,asc")
                .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(Collections.singletonList(converter.toUserResponseDTO(user1)))));
    }

    @Test
    @WithUserDetails("+375293000000")
    @DisplayName("getUserById with valid data - should return json with data about User with given id")
    public void getUserById1() throws Exception {
        // given
        Long userId = user1.getUserId();
        when(userService.getUserById(userId)).thenReturn(converter.toUserResponseDTO(user1));

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/users/get/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(converter.toUserResponseDTO(user1))))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+375293000000")
    @DisplayName("getUserById with invalid data - should handle DatabaseEntryNotFoundException, status 404")
    public void getUserById2() throws Exception {
        // given
        Long userId = 100500L;
        when(userService.getUserById(userId)).thenThrow(DatabaseEntryNotFoundException.class);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/users/get/" + userId))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof DatabaseEntryNotFoundException);
    }

    @Test
    @DisplayName("registerUser with valid data - shouldn't throw exceptions, status OK")
    public void registerUser1() throws Exception {
        // given
        UserCreateDTO userCreateDTO = getValidUserCreateDTO();
        UserResponseDTO userResponseDTO = converter.toUserResponseDTO(converter.fromUserCreateDTO(userCreateDTO));
        when(userService.createUser(userCreateDTO)).thenReturn(userResponseDTO);

        // when & then
        MvcResult mvcResult = mockMvc.perform(post("/users/register/")
                .content(toJson(userCreateDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userResponseDTO)))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @DisplayName("registerUser2 with invalid data (not unique phone) - should handle UserExistsException, status 419")
    public void registerUser2() throws Exception {
        // given
        UserCreateDTO userCreateDTO = getValidUserCreateDTO();
        when(userService.createUser(userCreateDTO)).thenThrow(UserExistsException.class);

        // when & then
        MvcResult mvcResult = mockMvc.perform(post("/users/register/")
                .content(toJson(userCreateDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof UserExistsException);
    }

    @Test
    @DisplayName("registerUser with invalid DTO fields - should handle BindException, status 400")
    public void registerUser3() throws Exception {
        // given
        UserCreateDTO userCreateDTO = getValidUserCreateDTO();
        userCreateDTO.setPhone("303"); // not valid length

        // when & then
        MvcResult mvcResult = mockMvc.perform(post("/users/register/")
                .content(toJson(userCreateDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof BindException);
    }

    @Test
    @WithUserDetails("+375295055055")
    @DisplayName("deleteUser with valid data - shouldn't throw exceptions, status OK")
    public void deleteUser() throws Exception {
        // given
        Long userId = user1.getUserId();
        doNothing().when(userService).deleteUser(userId);

        // when & then
        MvcResult mvcResult = mockMvc.perform(delete("/users/delete/" + userId))
                .andExpect(status().isOk())
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+375295055055")
    @DisplayName("deleteUser with invalid data (id) - should handle DatabaseEntryNotFoundException, status 404")
    public void deleteUser2() throws Exception {
        // given
        Long userId = 100500L;
        doThrow(DatabaseEntryNotFoundException.class).when(userService).deleteUser(userId);

        // when & then
        MvcResult mvcResult = mockMvc.perform(delete("/users/delete/" + userId))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof DatabaseEntryNotFoundException);
    }

    @Test
    @WithUserDetails("+375295055055")
    @DisplayName("deleteUser with invalid data (user with active Records) - " +
            "should handle DeletingClientWithActiveRecordsException, status 403")
    public void deleteUser3() throws Exception {
        // given
        Long userId = 100500L;
        doThrow(DeletingClientWithActiveRecordsException.class).when(userService).deleteUser(userId);

        // when & then
        MvcResult mvcResult = mockMvc.perform(delete("/users/delete/" + userId))
                .andExpect(status().isForbidden())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof DeletingClientWithActiveRecordsException);
    }

    @Test
    @WithUserDetails("+18465463222")
    @DisplayName("updateUserInfo with valid data - shouldn't throw exceptions, status 204")
    public void updateUserInfo1() throws Exception {
        // given
        UserUpdateDTO userUpdateDTO = getValidUserUpdateDTO();
        doNothing().when(userService).updateUserInfo(userUpdateDTO);

        // when & then
        MvcResult mvcResult = mockMvc.perform(put("/users/update")
                .content(toJson(userUpdateDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+18465463222")
    @DisplayName("updateUserInfo with invalid data (id) - should handle DatabaseEntryNotFoundException, status 404")
    public void updateUserInfo2() throws Exception {
        // given
        UserUpdateDTO userUpdateDTO = getValidUserUpdateDTO();
        doThrow(DatabaseEntryNotFoundException.class).when(userService).updateUserInfo(userUpdateDTO);

        // when & then
        MvcResult mvcResult = mockMvc.perform(put("/users/update")
                .content(toJson(userUpdateDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof DatabaseEntryNotFoundException);
    }

    @Test
    @WithUserDetails("+375293000000")
    @DisplayName("changeClientDiscount with valid data - shouldn't throw exception, status 204")
    public void changeClientDiscount() throws Exception {
        // given
        Long userId = user1.getUserId();
        Discount newDiscount = Discount.TEN;
        doNothing().when(userService).changeClientDiscount(userId, newDiscount);

        // when & then
        MvcResult mvcResult = mockMvc.perform(put("/users/change_discount")
                .param("clientId", userId.toString())
                .param("newDiscount", newDiscount.name()))
                .andExpect(status().isNoContent())
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+375295055055")
    @DisplayName("addRoleForUser with valid data - shouldn't throw exception, status OK")
    public void addRoleForUser1() throws Exception {
        // given
        Long userId = user1.getUserId();
        RoleDTO role = RoleDTO.builder().roleId(1).roleType(RoleType.ADMIN).build();

        doNothing().when(userService).addRoleForUser(userId, role);

        // when & then
        MvcResult mvcResult = mockMvc.perform(post("/users/add_role")
                .param("userId", userId.toString())
                .content(toJson(role))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+375295055055")
    @DisplayName("addRoleForUser with invalid data (user has the given role) - " +
            "should handle RoleManagementException exception, status 400")
    public void addRoleForUser2() throws Exception {
        // given
        Long userId = user1.getUserId();
        RoleDTO role = RoleDTO.builder().roleId(1).roleType(RoleType.ADMIN).build();
        doThrow(RoleManagementException.class).when(userService).addRoleForUser(userId, role);

        // when & then
        MvcResult mvcResult = mockMvc.perform(post("/users/add_role")
                .param("userId", userId.toString())
                .content(toJson(role))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(mvcResult.getResolvedException() instanceof RoleManagementException);
    }

    @Test
    @WithUserDetails("+375295055055")
    @DisplayName("revokeRole with valid data - shouldn't throw exception, status OK")
    public void revokeRole() throws Exception {
        // given
        Long userId = user1.getUserId();
        RoleDTO role = RoleDTO.builder().roleId(1).roleType(RoleType.ADMIN).build();

        doNothing().when(userService).revokeRole(userId, role);

        // when & then
        MvcResult mvcResult = mockMvc.perform(delete("/users/revoke_role")
                .param("userId", userId.toString())
                .content(toJson(role))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }


    private UserCreateDTO getValidUserCreateDTO() {
        return UserCreateDTO.builder()
                .firstName("test")
                .lastName("test")
                .phone("3034045056060")
                .password("somePassword")
                .build();
    }

    private UserUpdateDTO getValidUserUpdateDTO() {
        return UserUpdateDTO.builder()
                .userId(3L)
                .firstName("test")
                .build();
    }
}

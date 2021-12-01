package org.itrex.service.impl;

import org.itrex.dto.RoleDTO;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entity.Record;
import org.itrex.entity.Role;
import org.itrex.entity.User;
import org.itrex.entity.enums.Discount;
import org.itrex.entity.enums.RoleType;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;
import org.itrex.repository.RecordRepo;
import org.itrex.repository.UserRepo;
import org.itrex.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceImplTest {
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private RecordRepo recordRepo;
    @Autowired
    private UserService service;

    private final User user1 = new User(1L, "password1".getBytes(StandardCharsets.UTF_8),
            "Andrew", "T", "375293000000", "wow@gmail.com");
    private final User user4 = new User(4L, "password2".getBytes(StandardCharsets.UTF_8),
            "Staff", "Senior", "375295055055", "staff1@nailstudio.com");

    @Test
    @DisplayName("getAll - should return all UserResponseDTO with data equal to given Users")
    public void getAll() {
        // given
        when(userRepo.getAll()).thenReturn(Arrays.asList(user1, user4));

        // when
        List<UserResponseDTO> users = service.getAll();

        // then
        assertEquals(2, users.size());
        assertEquals(user1.getUserId(), users.get(0).getUserId());
        assertEquals(user1.getFirstName(), users.get(0).getFirstName());
        assertEquals(user1.getLastName(), users.get(0).getLastName());
        assertEquals(user1.getPhone(), users.get(0).getPhone());
        assertEquals(user1.getEmail(), users.get(0).getEmail());
        assertEquals(user4.getUserId(), users.get(1).getUserId());
        assertEquals(user4.getPhone(), users.get(1).getPhone());

        verify(userRepo).getAll();
    }

    @Test
    @DisplayName("getUserById with valid data - should return a UserCreateDTO with given id")
    public void getUserById1() throws DatabaseEntryNotFoundException {
        // given
        Long userId = 1L;
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));

        // when
        UserResponseDTO user = service.getUserById(userId);

        // then
        assertEquals(userId, user.getUserId());
        assertEquals(user1.getPhone(), user.getPhone());

        verify(userRepo).getUserById(userId);
    }

    @Test
    @DisplayName("getUserById with invalid data - should throw DatabaseEntryNotFoundException")
    public void getUserById2() {
        // given
        long userId = 7L; // there are no Users with this id
        when(userRepo.getUserById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getUserById(userId));

        verify(userRepo).getUserById(userId);
    }

    @Test
    @DisplayName("createUser with valid data - shouldn't throw exceptions and return UserResponseDTO should have " +
            "data equal to passed UserCreateDTO")
    public void createUser1() throws UserExistsException {
        // given
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .password("pass")
                .firstName("Freddy")
                .lastName("Krueger")
                .phone("1900909Fred")
                .build();
        User newUser = getNewUser();
        User createdUser = getNewUser();
        createdUser.setUserId(2L);

        when(userRepo.getUserByPhone(userCreateDTO.getPhone())).thenReturn(Optional.empty());
        when(userRepo.createUser(newUser)).thenReturn(createdUser);

        // when
        UserResponseDTO userResponseDTO = service.createUser(userCreateDTO);

        // then
        assertEquals(userCreateDTO.getPhone(), userResponseDTO.getPhone());
        assertEquals(2, userResponseDTO.getUserId());

        verify(userRepo).getUserByPhone(userCreateDTO.getPhone());
        verify(userRepo).createUser(newUser);
    }

    @Test
    @DisplayName("createUser with invalid data - users table shouldn't added users")
    public void createUser2() {
        // given
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .firstName(user1.getFirstName())
                .lastName(user1.getLastName())
                .phone(user1.getPhone())
                .build();
        String phone = user1.getPhone();
        when(userRepo.getUserByPhone(phone)).thenReturn(Optional.of(user1));

        // then & when
        assertThrows(UserExistsException.class, () -> service.createUser(userCreateDTO));

        verify(userRepo).getUserByPhone(userCreateDTO.getPhone());
        verify(userRepo, never()).createUser(user1);
    }

    @Test
    @DisplayName("deleteUser with valid data - shouldn't throw exceptions")
    public void deleteUser1() {
        // given
        Long userId = 1L;
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));
        when(recordRepo.getRecordsForClient(userId)).thenReturn(Collections.emptyList());
        doNothing().when(userRepo).deleteUser(user1);

        // when & then
        assertDoesNotThrow(() -> service.deleteUser(userId));

        verify(userRepo).getUserById(userId);
        verify(recordRepo).getRecordsForClient(userId);
        verify(userRepo).deleteUser(user1);
    }

    @Test
    @DisplayName("deleteUser with invalid data - should throw DeletingClientWithActiveRecordsException")
    public void deleteUser2() {
        // given
        Long userId = 1L; // this User has 1 future record
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));
        when(recordRepo.getRecordsForClient(userId)).thenReturn(Collections.singletonList(Record.builder()
                .date(LocalDate.of(2050, 12, 31))
                .build()));

        // when & then
        assertThrows(DeletingClientWithActiveRecordsException.class, () -> service.deleteUser(userId));

        verify(userRepo).getUserById(userId);
        verify(recordRepo).getRecordsForClient(userId);
        verify(userRepo, never()).deleteUser(user1);
    }

    @Test
    @DisplayName("updateUserInfo with valid data - shouldn't throw exceptions, should invoke repo method")
    public void updateUserInfo1() {
        // given
        Long userId = 1L;
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));
        doNothing().when(userRepo).updateUserInfo(user1);

        String newEmail = "my_new_email@mail.ru";
        UserUpdateDTO user = UserUpdateDTO.builder()
                .userId(userId)
                .email(newEmail)
                .build();

        // when & then
        assertDoesNotThrow(() -> service.updateUserInfo(user));

        verify(userRepo).getUserById(userId);
        verify(userRepo).updateUserInfo(user1);
    }

    @Test
    @DisplayName("updateUserInfo with data equal to User's data - shouldn't throw exceptions, shouldn't invoke repo method")
    public void updateUserInfo2() {
        // given
        Long userId = 1L;
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));

        UserUpdateDTO user = UserUpdateDTO.builder()
                .userId(user1.getUserId())
                .firstName(user1.getFirstName())
                .lastName(user1.getLastName())
                .email(user1.getEmail())
                .build();

        // when & then
        assertDoesNotThrow(() -> service.updateUserInfo(user));

        verify(userRepo).getUserById(userId);
        verify(userRepo, never()).updateUserInfo(user1);
    }

    @Test
    @DisplayName("changeClientDiscount with valid data - shouldn't throw exceptions")
    public void changeClientDiscount() {
        // given
        Long userId = 1L;
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));
        Discount oldDiscount = user1.getDiscount();
        Discount newDiscount = Discount.TEN;

        // when & then
        assertDoesNotThrow(() -> service.changeClientDiscount(userId, oldDiscount)); // shouldn't call repo method
        assertDoesNotThrow(() -> service.changeClientDiscount(userId, newDiscount));

        verify(userRepo, times(2)).getUserById(userId);
        verify(userRepo).updateUserInfo(user1);
    }

    @Test
    @DisplayName("addRoleForUser with valid data - shouldn't throw exceptions")
    public void addRoleForUser1() {
        // given
        Long userId = 1L;
        user1.setUserRoles(new HashSet<>(Collections.singletonList(new Role(1, RoleType.ADMIN))));
        RoleDTO role = new RoleDTO(2, RoleType.STAFF);
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));

        // when & then
        assertDoesNotThrow(() -> service.addRoleForUser(userId, role));

        verify(userRepo).getUserById(userId);
        verify(userRepo).updateUserInfo(user1);

        user1.setUserRoles(null);
    }

    @Test
    @DisplayName("addRoleForUser with invalid data - should throw RoleManagementException")
    public void addRoleForUser2() {
        // given
        Long userId = 1L;
        user1.setUserRoles(new HashSet<>(Collections.singletonList(new Role(1, RoleType.ADMIN))));
        RoleDTO role = new RoleDTO(1, RoleType.ADMIN);
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));

        // when & then
        assertThrows(RoleManagementException.class, () -> service.addRoleForUser(userId, role));

        verify(userRepo).getUserById(userId);
        verify(userRepo, never()).updateUserInfo(user1);

        user1.setUserRoles(null);
    }

    @Test
    @DisplayName("revokeRole with valid data - shouldn't throw exceptions")
    public void revokeRole1() {
        // given
        Long userId = 1L;
        user1.setUserRoles(new HashSet<>(Arrays.asList(new Role(1, RoleType.ADMIN), new Role(2, RoleType.STAFF))));
        RoleDTO role = new RoleDTO(1, RoleType.ADMIN);
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));

        // when & then
        assertDoesNotThrow(() -> service.revokeRole(userId, role));

        verify(userRepo).getUserById(userId);
        verify(userRepo).updateUserInfo(user1);

        user1.setUserRoles(null);
    }

    @Test
    @DisplayName("revokeRole with invalid data - should throw RoleManagementException")
    public void revokeRole2() {
        // given
        Long userId = 1L;
        user1.setUserRoles(new HashSet<>(Collections.singletonList(new Role(1, RoleType.ADMIN))));
        RoleDTO role = new RoleDTO(1, RoleType.ADMIN);
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(user1));

        // when & then
        assertThrows(RoleManagementException.class, () -> service.revokeRole(userId, role));

        verify(userRepo).getUserById(userId);
        verify(userRepo, never()).updateUserInfo(user1);

        user1.setUserRoles(null);
    }

    private User getNewUser() {
        return User.builder()
                .firstName("Freddy")
                .lastName("Krueger")
                .phone("1900909Fred")
                .email("freshmeat@yahoo.com")
                .build();
    }
}

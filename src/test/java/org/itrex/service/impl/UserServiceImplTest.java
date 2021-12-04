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
import org.itrex.repository.data.RecordRepository;
import org.itrex.repository.data.UserRepository;
import org.itrex.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceImplTest {
    @MockBean
    private UserRepository userRepo;
    @MockBean
    private RecordRepository recordRepo;
    @Autowired
    private UserService service;

    private final User user1 = new User(1L, "password1",
            "Andrew", "T", "375293000000", "wow@gmail.com");
    private final User user4 = new User(4L, "password2",
            "Staff", "Senior", "375295055055", "staff1@nailstudio.com");

    @Test
    @DisplayName("getAll - should return all UserResponseDTO with data equal to given Users")
    public void getAll() {
        // given
        List<User> someUsers = Arrays.asList(user1, user4);
        when(userRepo.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(someUsers));
        when(userRepo.findAll(Pageable.ofSize(1))).thenReturn(new PageImpl<>(Collections.singletonList(user1)));

        // when
        List<UserResponseDTO> users = service.getAll(Pageable.unpaged());
        List<UserResponseDTO> users2 = service.getAll(Pageable.ofSize(1));

        // then
        assertEquals(someUsers.size(), users.size());
        assertEquals(user1.getUserId(), users.get(0).getUserId());
        assertEquals(user1.getFirstName(), users.get(0).getFirstName());
        assertEquals(user1.getLastName(), users.get(0).getLastName());
        assertEquals(user1.getPhone(), users.get(0).getPhone());
        assertEquals(user1.getEmail(), users.get(0).getEmail());
        assertEquals(user4.getUserId(), users.get(1).getUserId());
        assertEquals(user4.getPhone(), users.get(1).getPhone());

        assertEquals(1, users2.size());
        assertEquals(user1.getUserId(), users2.get(0).getUserId());
        assertEquals(user1.getPhone(), users2.get(0).getPhone());

        verify(userRepo).findAll(Pageable.unpaged());
        verify(userRepo).findAll(Pageable.ofSize(1));
    }

    @Test
    @DisplayName("getUserById with valid data - should return a UserCreateDTO with given id")
    public void getUserById1() throws DatabaseEntryNotFoundException {
        // given
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));

        // when
        UserResponseDTO user = service.getUserById(userId);

        // then
        assertEquals(userId, user.getUserId());
        assertEquals(user1.getPhone(), user.getPhone());

        verify(userRepo).findById(userId);
    }

    @Test
    @DisplayName("getUserById with invalid data - should throw DatabaseEntryNotFoundException")
    public void getUserById2() {
        // given
        long userId = 7L; // there are no Users with this id
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getUserById(userId));

        verify(userRepo).findById(userId);
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

        when(userRepo.findByPhone(userCreateDTO.getPhone())).thenReturn(Optional.empty());
        when(userRepo.save(newUser)).thenReturn(createdUser);

        // when
        UserResponseDTO userResponseDTO = service.createUser(userCreateDTO);

        // then
        assertEquals(userCreateDTO.getPhone(), userResponseDTO.getPhone());
        assertEquals(2, userResponseDTO.getUserId());

        verify(userRepo).findByPhone(userCreateDTO.getPhone());
        verify(userRepo).save(newUser);
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
        when(userRepo.findByPhone(phone)).thenReturn(Optional.of(user1));

        // then & when
        assertThrows(UserExistsException.class, () -> service.createUser(userCreateDTO));

        verify(userRepo).findByPhone(userCreateDTO.getPhone());
        verify(userRepo, never()).save(user1);
    }

    @Test
    @DisplayName("deleteUser with valid data - shouldn't throw exceptions")
    public void deleteUser1() {
        // given
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));
        when(recordRepo.getByClientUserId(userId)).thenReturn(Collections.emptyList());
        doNothing().when(userRepo).delete(user1);

        // when & then
        assertDoesNotThrow(() -> service.deleteUser(userId));

        verify(userRepo).findById(userId);
        verify(recordRepo).getByClientUserId(userId);
        verify(userRepo).delete(user1);
    }

    @Test
    @DisplayName("deleteUser with invalid data - should throw DeletingClientWithActiveRecordsException")
    public void deleteUser2() {
        // given
        Long userId = 1L; // this User has 1 future record
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));
        when(recordRepo.getByClientUserId(userId)).thenReturn(Collections.singletonList(Record.builder()
                .date(LocalDate.of(2050, 12, 31))
                .build()));

        // when & then
        assertThrows(DeletingClientWithActiveRecordsException.class, () -> service.deleteUser(userId));

        verify(userRepo).findById(userId);
        verify(recordRepo).getByClientUserId(userId);
        verify(userRepo, never()).delete(user1);
    }

    @Test
    @DisplayName("updateUserInfo with valid data - shouldn't throw exceptions, should invoke repo method")
    public void updateUserInfo1() {
        // given
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));
        when(userRepo.save(user1)).thenReturn(user1);

        String newEmail = "my_new_email@mail.ru";
        UserUpdateDTO user = UserUpdateDTO.builder()
                .userId(userId)
                .email(newEmail)
                .build();

        // when & then
        assertDoesNotThrow(() -> service.updateUserInfo(user));

        verify(userRepo).findById(userId);
        verify(userRepo).save(user1);
    }

    @Test
    @DisplayName("updateUserInfo with data equal to User's data - shouldn't throw exceptions, shouldn't invoke repo method")
    public void updateUserInfo2() {
        // given
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));

        UserUpdateDTO user = UserUpdateDTO.builder()
                .userId(user1.getUserId())
                .firstName(user1.getFirstName())
                .lastName(user1.getLastName())
                .email(user1.getEmail())
                .build();

        // when & then
        assertDoesNotThrow(() -> service.updateUserInfo(user));

        verify(userRepo).findById(userId);
        verify(userRepo, never()).save(user1);
    }

    @Test
    @DisplayName("changeClientDiscount with valid data - shouldn't throw exceptions")
    public void changeClientDiscount() {
        // given
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));
        Discount oldDiscount = user1.getDiscount();
        Discount newDiscount = Discount.TEN;

        // when & then
        assertDoesNotThrow(() -> service.changeClientDiscount(userId, oldDiscount)); // shouldn't call repo method
        assertDoesNotThrow(() -> service.changeClientDiscount(userId, newDiscount));

        verify(userRepo, times(2)).findById(userId);
        verify(userRepo).save(user1);
    }

    @Test
    @DisplayName("addRoleForUser with valid data - shouldn't throw exceptions")
    public void addRoleForUser1() {
        // given
        Long userId = 1L;
        user1.setUserRoles(new HashSet<>(Collections.singletonList(new Role(1, RoleType.ADMIN))));
        RoleDTO role = new RoleDTO(2, RoleType.STAFF);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));

        // when & then
        assertDoesNotThrow(() -> service.addRoleForUser(userId, role));

        verify(userRepo).findById(userId);
        verify(userRepo).save(user1);

        user1.setUserRoles(null);
    }

    @Test
    @DisplayName("addRoleForUser with invalid data - should throw RoleManagementException")
    public void addRoleForUser2() {
        // given
        Long userId = 1L;
        user1.setUserRoles(new HashSet<>(Collections.singletonList(new Role(1, RoleType.ADMIN))));
        RoleDTO role = new RoleDTO(1, RoleType.ADMIN);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));

        // when & then
        assertThrows(RoleManagementException.class, () -> service.addRoleForUser(userId, role));

        verify(userRepo).findById(userId);
        verify(userRepo, never()).save(user1);

        user1.setUserRoles(null);
    }

    @Test
    @DisplayName("revokeRole with valid data - shouldn't throw exceptions")
    public void revokeRole1() {
        // given
        Long userId = 1L;
        user1.setUserRoles(new HashSet<>(Arrays.asList(new Role(1, RoleType.ADMIN), new Role(2, RoleType.STAFF))));
        RoleDTO role = new RoleDTO(1, RoleType.ADMIN);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));

        // when & then
        assertDoesNotThrow(() -> service.revokeRole(userId, role));

        verify(userRepo).findById(userId);
        verify(userRepo).save(user1);

        user1.setUserRoles(null);
    }

    @Test
    @DisplayName("revokeRole with invalid data - should throw RoleManagementException")
    public void revokeRole2() {
        // given
        Long userId = 1L;
        user1.setUserRoles(new HashSet<>(Collections.singletonList(new Role(1, RoleType.ADMIN))));
        RoleDTO role = new RoleDTO(1, RoleType.ADMIN);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));

        // when & then
        assertThrows(RoleManagementException.class, () -> service.revokeRole(userId, role));

        verify(userRepo).findById(userId);
        verify(userRepo, never()).save(user1);

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

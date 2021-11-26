package org.itrex.service.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.itrex.TestBaseHibernate;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entity.Record;
import org.itrex.entity.User;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.UserExistsException;
import org.itrex.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest extends TestBaseHibernate {
    @Autowired
    private UserService service;
    private final int usersTableInitialTestSize = 4;
    private Session session;

    @Test
    @DisplayName("getAll - should return all UserResponseDTO equal to testdata migration script")
    public void getAll() {
        // given & when
        List<UserResponseDTO> users = service.getAll();

        // then
        users.forEach(System.out::println);
        assertEquals(usersTableInitialTestSize, users.size());
        assertEquals(1, users.get(0).getUserId());
        assertEquals("Andrew", users.get(0).getFirstName());
        assertEquals("T", users.get(0).getLastName());
        assertEquals("+375293000000", users.get(0).getPhone());
        assertEquals("wow@gmail.com", users.get(0).getEmail());
        assertEquals(3, users.get(2).getUserId());
        assertEquals("+1946484888", users.get(2).getPhone());
    }

    @Test
    @DisplayName("getUserById with valid data - should return a UserCreateDTO with given id")
    public void getUserById1() {
        // given
        Long userId = 1L;

        // when
        UserResponseDTO user = service.getUserById(userId);

        // then
        assertEquals(userId, user.getUserId());
        assertEquals("+375293000000", user.getPhone());
    }

    @Test
    @DisplayName("getUserById with invalid data - should throw DatabaseEntryNotFoundException")
    public void getUserById2() {
        // given
        long userId = 7L; // there are no Users with this id

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getUserById(userId));
    }

    @Test
    @DisplayName("createUser with valid data - users table should contain given User")
    public void createUser1() throws UserExistsException {
        // given
        UserCreateDTO user = UserCreateDTO.builder()
                .password("notSoStrongPassword")
                .firstName("Freddy")
                .lastName("Krueger")
                .phone("1900909Fred")
                .email("freshmeat@yahoo.com")
                .build();

        // when
        Long createdUserId = service.createUser(user);

        // then
        session = getSessionFactory().openSession();

        assertEquals(usersTableInitialTestSize + 1,
                session.createQuery("FROM User", User.class).list().size());

        long usersCount = session.createQuery("FROM User", User.class).list().stream()
                .filter(u -> u.getPhone().equals("1900909Fred")).count();
        assertEquals(1, usersCount);

        assertEquals(usersTableInitialTestSize + 1, createdUserId);

        session.close();
    }

    @Test
    @DisplayName("createUser with invalid data - users table shouldn't added users")
    public void createUser2() {
        // given
        UserCreateDTO user = UserCreateDTO.builder()
                .password("notSoStrongPassword")
                .firstName("Edward")
                .lastName("Scissorshands")
                // should be unique
                .phone("+375293000000")
                .email("holdme@yahoo.com")
                .build();

        // then & when
        assertThrows(UserExistsException.class, () -> service.createUser(user));
    }

    @Test
    @DisplayName("deleteUser with valid data - should delete User, all Records for User should be deleted")
    public void deleteUser() throws DeletingClientWithActiveRecordsException {
        // given
        Long userId = 1L;

        // when
        service.deleteUser(userId);

        // then
        session = getSessionFactory().openSession();

        assertNull(session.find(User.class, userId));

        Query<Record> query = session.createQuery("FROM Record WHERE user_id = :userId", Record.class);
        query.setParameter("userId", userId);
        assertTrue(query.list().isEmpty());

        session.close();
    }

    @Test
    @DisplayName("updateUserInfo with valid data - edited fields should be changed for a User with given id")
    public void updateUserInfo() {
        // given
        Long userId = 1L;
        String newEmail = "my_new_email@mail.ru";
        UserUpdateDTO user = UserUpdateDTO.builder()
                .userId(userId)
                .firstName("Andrew")
                .lastName("T")
                .email(newEmail)
                .build();

        // when
        service.updateUserInfo(user);

        // then
        session = getSessionFactory().openSession();
        assertEquals(newEmail, session.find(User.class, 1L).getEmail());
        session.close();
    }

    @Test
    @DisplayName("changeClientDiscount with valid data - 'discount' field should be changed for a User with given ID")
    public void changeClientDiscount() {
        // given
        Long userId = 1L;
        Discount newDiscount = Discount.TEN;

        // when
        service.changeClientDiscount(userId, newDiscount);

        // then
        session = getSessionFactory().openSession();
        assertEquals(newDiscount, session.find(User.class, 1L).getDiscount());
        session.close();
    }

    @Test
    @DisplayName("addRoleForUser with valid data - should add 1 row into ManyToMany join table")
    public void addRoleForUser() {
        // given
        Long userId = 2L; // this User have 1 role, doesn't have "staff" role
        String roleName = "staff"; // 2 Users have this role

        // when
        service.addRoleForUser(userId, roleName);

        // then
        session = getSessionFactory().openSession();

        assertEquals(2, session.get(User.class, userId).getUserRoles().size());
        assertEquals(7, session.createSQLQuery("SELECT * FROM users_roles").list().size());

        session.close();
    }
}

package org.itrex.services.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.itrex.TestBaseHibernate;
import org.itrex.dto.UserDTO;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.exceptions.DatabaseEntryNotFoundException;
import org.itrex.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest extends TestBaseHibernate {
    private final UserService service = getContext().getBean(UserService.class);
    private final int usersTableInitialTestSize = 3;
    private Session session;

    @Test
    @DisplayName("getAll - should return 3 UserDTO equal to testdata migration script")
    public void getAll() {
        // given & when
        List<UserDTO> users = service.getAll();

        // then
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
    @DisplayName("findUserById with valid data - should return a UserDTO with given id")
    public void findUserById1() {
        // given
        long userId = 1L;

        // when
        UserDTO user = service.findUserById(userId);

        // then
        assertEquals(userId, user.getUserId());
        assertEquals("+375293000000", user.getPhone());
    }

    @Test
    @DisplayName("findUserById with invalid data - should throw DatabaseEntryNotFoundException")
    public void findUserById2() {
        // given
        long userId = 7L; // there are no Users with this id

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.findUserById(userId));
    }

    @Test
    @DisplayName("findUserByPhone with valid data - should return a UserВЕЩ with given phone number")
    public void findUserByPhone1() {
        // given
        String phone = "+1946484888";

        // when
        UserDTO user = service.findUserByPhone(phone);

        // then
        assertEquals(phone, user.getPhone());
        assertEquals("hughjackman@gmail.com", user.getEmail());
        assertEquals(3, user.getUserId());
    }

    @Test
    @DisplayName("findUserByPhone with invalid data - should return null")
    public void findUserByPhone2() {
        // given
        String phone = "+7777777777"; // there are no Users with this phone number

        // when
        UserDTO user = service.findUserByPhone(phone);

        // when & then
        assertNull(user);
    }

    @Test
    @DisplayName("addUser with valid data - users table should contain given User")
    public void addUser1() {
        // given
        UserDTO user = UserDTO.builder()
                .password("notSoStrongPassword")
                .firstName("Freddy")
                .lastName("Krueger")
                .phone("1900909Fred")
                .email("freshmeat@yahoo.com")
                .build();

        // when
        service.addUser(user);

        // then
        session = getSessionFactory().openSession();

        assertEquals(usersTableInitialTestSize + 1,
                session.createQuery("FROM User", User.class).list().size());

        long usersCount = session.createQuery("FROM User", User.class).list().stream()
                .filter(u -> u.getPhone().equals("1900909Fred")).count();
        assertEquals(1, usersCount);

        session.close();
    }

    @Test
    @DisplayName("addUser with invalid data - users table shouldn't added users")
    public void addUser2() {
        // given
        UserDTO user2 = UserDTO.builder()
                .password("notSoStrongPassword")
                .firstName("Edward")
                .lastName("Scissorshands")
                // should be unique
                .phone("+375293000000")
                .email("holdme@yahoo.com")
                .build();

        // when
        String message = service.addUser(user2);

        // then
        assertEquals("User with the same phone number already exists!", message);
        session = getSessionFactory().openSession();
        assertEquals(usersTableInitialTestSize,
                session.createQuery("FROM User", User.class).list().size());
        session.close();
    }

    @Test
    @DisplayName("deleteUser with valid data - should delete User, all Records for User should be deleted")
    public void deleteUser() {
        // given
        long userId = 1L;

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
    @DisplayName("changeEmail with valid data - 'e_mail' field should be changed for a User with given ID")
    public void changeEmail() {
        // given
        long userId = 1L;
        String newEmail = "my_new_email@mail.ru";

        // when
        service.changeEmail(userId, newEmail);

        // then
        session = getSessionFactory().openSession();
        assertEquals(newEmail, session.find(User.class, 1L).getEmail());
        session.close();
    }

    @Test
    @DisplayName("changeDiscount with valid data - 'discount' field should be changed for a User with given ID")
    public void changeDiscount() {
        // given
        long userId = 1L;
        Discount newDiscount = Discount.TEN;

        // when
        service.changeDiscount(userId, newDiscount);

        // then
        session = getSessionFactory().openSession();
        assertEquals(newDiscount, session.find(User.class, 1L).getDiscount());
        session.close();
    }

    @Test
    @DisplayName("addRoleForUser with valid data - should add 1 row into ManyToMany join table")
    public void addRoleForUser() {
        // given
        session = getSessionFactory().openSession();
        long userId = 1L; // this User have 2 roles, doesn't have "client" role
        String roleName = "client"; // 2 Users have this role
        session.close();

        // when
        service.addRoleForUser(userId, roleName);

        // then
        session = getSessionFactory().openSession();

        assertEquals(3, session.get(User.class, 1L).getUserRoles().size());
        assertEquals(5, session.createSQLQuery("SELECT * FROM users_roles").list().size());

        session.close();
    }
}

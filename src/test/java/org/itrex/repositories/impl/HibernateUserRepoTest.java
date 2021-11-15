package org.itrex.repositories.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.itrex.TestBaseHibernate;
import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.exceptions.DatabaseEntryNotFoundException;
import org.itrex.repositories.UserRepo;
import org.itrex.util.PasswordEncryption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUserRepoTest extends TestBaseHibernate {
    private final UserRepo repo = getContext().getBean(UserRepo.class);
    private final int usersTableInitialTestSize = 3;
    private Session session;

    @Test
    @DisplayName("getAll - should return 3 Users equal to testdata migration script")
    public void getAll() {
        // given & when
        List<User> users = repo.getAll();

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
    @DisplayName("getUserById with valid data - should return a User with given id")
    public void getUserById1() {
        // given
        long userId = 1L;

        // when
        User user = repo.getUserById(userId);

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
        assertThrows(DatabaseEntryNotFoundException.class, () -> repo.getUserById(userId));
    }

    @Test
    @DisplayName("getUserByPhone with valid data - should return a User with given phone number")
    public void getUserByPhone1() {
        // given
        String phone = "+1946484888";

        // when
        User user = repo.getUserByPhone(phone);

        // then
        assertEquals(phone, user.getPhone());
        assertEquals("hughjackman@gmail.com", user.getEmail());
        assertEquals(3, user.getUserId());
    }

    @Test
    @DisplayName("getUserByPhone with invalid data - should return null")
    public void getUserByPhone2() {
        // given
        String phone = "+7777777777"; // there are no Users with this phone number

        // when
        User user = repo.getUserByPhone(phone);

        // when & then
        assertNull(user);
    }

    @Test
    @DisplayName("addUser with valid data - users table should contain given User")
    public void addUser1() {
        // given
        User user = User.builder()
                .password(PasswordEncryption.getEncryptedPassword("notSoStrongPassword"))
                .firstName("Freddy")
                .lastName("Krueger")
                .phone("1900909Fred")
                .email("freshmeat@yahoo.com")
                .build();

        // when
        repo.addUser(user);

        // then
        session = getSessionFactory().openSession();
        long usersCount = session.createQuery("FROM User", User.class).list().stream()
                .filter(u -> u.getPhone().equals("1900909Fred")).count();
        assertEquals(1, usersCount);
        session.close();
    }

    @Test
    @DisplayName("addUser with invalid data - should throw HibernateException")
    public void addUser2() {
        // given
        User user = User.builder()
                .password(PasswordEncryption.getEncryptedPassword("notSoStrongPassword"))
                .firstName("Freddy")
                .lastName("Krueger")
                // shouldn't exceed 13 chars
                .phone("+375-29-333-33-33")
                .email("freshmeat@yahoo.com")
                .build();

        // when & then
        assertThrows(HibernateException.class, () -> repo.addUser(user));
    }

    @Test
    @DisplayName("deleteUser with valid data - should delete User, all Records for User should be deleted")
    public void deleteUser() {
        // given
        session = getSessionFactory().openSession();
        long userId = 1L;
        User user = session.find(User.class, userId); // this User has 2 records
        session.close();

        // when
        repo.deleteUser(user);

        // then
        session = getSessionFactory().openSession();
        assertNull(session.find(User.class, userId));

        Query<Record> query = session.createQuery("FROM Record WHERE user_id = :userId", Record.class);
        query.setParameter("userId", userId);
        assertTrue(query.list().isEmpty());
        session.close();
    }

    @Test
    @DisplayName("changeEmail with valid data - 'e_mail' field should be changed for a User")
    public void changeEmail() {
        // given
        session = getSessionFactory().openSession();
        User user = session.find(User.class, 1L);
        session.close();
        String newEmail = "my_new_email@mail.ru";

        // when
        repo.changeEmail(user, newEmail);

        // then
        session = getSessionFactory().openSession();
        user = session.find(User.class, 1L);
        assertEquals(newEmail, user.getEmail());
        session.close();
    }

    @Test
    @DisplayName("changeDiscount with valid data - 'discount' field should be changed for a User")
    public void changeDiscount() {
        // given
        session = getSessionFactory().openSession();
        User user = session.find(User.class, 1L);
        session.close();
        Discount newDiscount = Discount.TEN;

        // when
        repo.changeDiscount(user, newDiscount);

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
        Role client = session.get(Role.class, 3L); // 2 Users have this role
        User user = session.get(User.class, 1L); // this User have 2 roles, doesn't have "client" role
        session.close();

        // when
        repo.addRoleForUser(user, client);

        // then
        session = getSessionFactory().openSession();
        assertEquals(3, session.get(User.class, 1L).getUserRoles().size());
        assertEquals(5, session.createSQLQuery("SELECT * FROM users_roles").list().size());
        session.close();
    }
}
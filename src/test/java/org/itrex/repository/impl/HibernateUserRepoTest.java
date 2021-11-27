package org.itrex.repository.impl;

import org.hibernate.Session;
import org.itrex.TestBaseHibernate;
import org.itrex.entity.Record;
import org.itrex.entity.Role;
import org.itrex.entity.User;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.repository.UserRepo;
import org.itrex.util.PasswordEncryption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUserRepoTest extends TestBaseHibernate {
    @Autowired
    private UserRepo repo;
    private final int usersTableInitialTestSize = 4;
    private Session session;

    @Test
    @DisplayName("getAll - should return all Users equal to testdata migration script")
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
        Long userId = 1L;

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
        Long userId = 7L; // there are no Users with this id

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

        // then
        assertNull(user);
    }

    @Test
    @DisplayName("createUser with valid data - users table should contain given User")
    public void createUser1() {
        // given
        User user = User.builder()
                .password(PasswordEncryption.getEncryptedPassword("notSoStrongPassword"))
                .firstName("Freddy")
                .lastName("Krueger")
                .phone("1900909Fred")
                .email("freshmeat@yahoo.com")
                .build();

        // when
        User returnedUser = repo.createUser(user);

        // then
        session = getSessionFactory().openSession();
        List<User> users = session.createQuery("FROM User", User.class).list();
        assertEquals(usersTableInitialTestSize + 1, users.size());
        assertEquals(1, users.stream()
                .filter(u -> u.getPhone().equals("1900909Fred")).count());
        assertEquals("1900909Fred", returnedUser.getPhone());
        session.close();
    }

    @Test
    @DisplayName("createUser with invalid data - should throw an Exception")
    public void createUser2() {
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
        assertThrows(Exception.class, () -> repo.createUser(user));
    }

    @Test
    @DisplayName("deleteUser with valid data - should delete User, all Records for User as client should be deleted")
    public void deleteUser() {
        // given
        session = getSessionFactory().openSession();
        Long userId = 1L;
        User user = session.find(User.class, userId); // this User has 2 records as client and 2 records as staff
        session.close();

        // when
        repo.deleteUser(user);

        // then
        session = getSessionFactory().openSession();
        assertNull(session.find(User.class, userId));
        List<Record> recordsForClient = session.createQuery("FROM Record WHERE client_id = :id", Record.class)
                .setParameter("id", userId)
                .list();
        assertTrue(recordsForClient.isEmpty());

        List<Record> recordsForStaff = session.createQuery("FROM Record WHERE staff_id = :id", Record.class)
                .setParameter("id", userId)
                .list();
        assertTrue(recordsForStaff.isEmpty());

        session.close();
    }

    @Test
    @DisplayName("updateUserInfo with valid data - updated fields should be changed for a User")
    public void updateUserInfo() {
        // given
        session = getSessionFactory().openSession();
        User user = session.find(User.class, 1L);
        session.close();
        String newEmail = "my_new_email@mail.ru";

        // when
        user.setEmail(newEmail);
        repo.updateUserInfo(user);

        // then
        session = getSessionFactory().openSession();
        user = session.find(User.class, 1L);
        assertEquals(newEmail, user.getEmail());
        session.close();
    }
}
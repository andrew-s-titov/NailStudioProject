package org.itrex.repository.impl;

import org.hibernate.Session;
import org.itrex.TestBaseHibernateRepository;
import org.itrex.entity.Record;
import org.itrex.entity.User;
import org.itrex.repository.UserRepo;
import org.itrex.util.PasswordEncryption;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Using deprecated repository class")
public class HibernateUserRepoTest extends TestBaseHibernateRepository {
    @Autowired
    private UserRepo repo;
    private final int usersTableInitialTestSize = 4;
    private Session session;

    @Test
    @DisplayName("findAll - should return all Users equal to testdata migration script")
    public void findAll() {
        // given & when
        List<User> users = repo.findAll();

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
    @DisplayName("findById with valid data - should return a User with given id")
    public void findById1() {
        // given
        Long userId = 1L;

        // when
        User user = repo.findById(userId).get();

        // then
        assertEquals(userId, user.getUserId());
        assertEquals("+375293000000", user.getPhone());
    }

    @Test
    @DisplayName("findById with invalid data - should throw DatabaseEntryNotFoundException")
    public void findById2() {
        // given
        Long userId = 7L; // there are no Users with this id

        // when
        Optional<User> userById = repo.findById(userId);

        // when & then
        assertTrue(userById.isEmpty());
    }

    @Test
    @DisplayName("findByPhone with valid data - should return a User with given phone number")
    public void findByPhone1() {
        // given
        String phone = "+1946484888";

        // when
        User user = repo.findByPhone(phone).get();

        // then
        assertEquals(phone, user.getPhone());
        assertEquals("hughjackman@gmail.com", user.getEmail());
        assertEquals(3, user.getUserId());
    }

    @Test
    @DisplayName("findByPhone with invalid data - should return null")
    public void findByPhone2() {
        // given
        String phone = "+7777777777"; // there are no Users with this phone number

        // when
        Optional<User> userByPhone = repo.findByPhone(phone);

        // then
        assertTrue(userByPhone.isEmpty());
    }

    @Test
    @DisplayName("save with valid data - users table should contain given User")
    public void save1() {
        // given
        User user = User.builder()
                .password("notSoStrongPassword")
                .firstName("Freddy")
                .lastName("Krueger")
                .phone("1900909Fred")
                .email("freshmeat@yahoo.com")
                .build();

        // when
        User returnedUser = repo.save(user);

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
    @DisplayName("save with invalid data - should throw an Exception")
    public void save2() {
        // given
        User user = User.builder()
                .password("notSoStrongPassword")
                .firstName("Freddy")
                .lastName("Krueger")
                // shouldn't exceed 13 chars
                .phone("+375-29-333-33-33")
                .email("freshmeat@yahoo.com")
                .build();

        // when & then
        assertThrows(Exception.class, () -> repo.save(user));
    }

    @Test
    @DisplayName("delete with valid data - should delete User, all Records for User as client should be deleted, " +
            "all Records for User as staff should remain with FK set to NULL")
    public void delete() {
        // given
        session = getSessionFactory().openSession();
        Long userId = 1L;
        User user = session.find(User.class, userId); // this User has 2 records as client and 2 records as staff
        session.close();

        // when
        repo.delete(user);

        // then
        session = getSessionFactory().openSession();
        assertNull(session.find(User.class, userId));
        assertTrue(session.createQuery("FROM Record WHERE client_id = :id", Record.class)
                .setParameter("id", userId)
                .list()
                .isEmpty());

        List<Record> records = session.createQuery("FROM Record", Record.class).list();
        assertEquals(2, records.size());
        assertEquals(2, records.stream().map(Record::getStaff).filter(Objects::isNull).count());

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
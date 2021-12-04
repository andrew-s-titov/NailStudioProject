package org.itrex.repository.data;

import org.itrex.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private final int usersTableInitialTestSize = 4;

    @Test
    @DisplayName("findAll - should return all Users equal to testdata migration script")
    public void findAll() {
        // given & when
        Iterable<User> users = userRepository.findAll();
        Iterator<User> iterator = users.iterator();

        // then
        User user1 = iterator.next();
        iterator.next();
        User user3 = iterator.next();
        assertEquals(usersTableInitialTestSize, StreamSupport.stream(users.spliterator(), false).count());
        assertEquals(1, user1.getUserId());
        assertEquals("Andrew", user1.getFirstName());
        assertEquals("T", user1.getLastName());
        assertEquals("+375293000000", user1.getPhone());
        assertEquals("wow@gmail.com", user1.getEmail());
        assertEquals(3, user3.getUserId());
        assertEquals("+1946484888", user3.getPhone());
    }

    @Test
    @DisplayName("findById with valid data - should return a User with given id")
    public void findById1() {
        // given
        Long userId = 1L;

        // when
        User user = userRepository.findById(userId).get();

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
        Optional<User> userById = userRepository.findById(userId);

        // when & then
        assertTrue(userById.isEmpty());
    }

    @Test
    @DisplayName("findByPhone with valid data - should return a User with given phone number")
    public void findByPhone1() {
        // given
        String phone = "+1946484888";

        // when
        User user = userRepository.findByPhone(phone).get();

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
        Optional<User> userByPhone = userRepository.findByPhone(phone);

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
        User returnedUser = userRepository.save(user);

        // then
        assertNotNull(returnedUser);
        assertEquals(returnedUser.getPhone(), user.getPhone());
        assertEquals(usersTableInitialTestSize + 1, userRepository.count());
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
        assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    @DisplayName("delete with valid data - should delete User, all Records for User as client should be deleted, " +
            "all Records for User as staff should remain with FK set to NULL")
    public void delete() {
        // given
        Long userId = 1L;
        User user = userRepository.findById(userId).get();

        // when & then
        assertDoesNotThrow(() -> userRepository.delete(user));
        assertEquals(usersTableInitialTestSize - 1, userRepository.count());
    }
}

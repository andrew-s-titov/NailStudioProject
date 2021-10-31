package org.itrex.repositories.impl;

import org.hibernate.query.Query;
import org.itrex.TestBaseHibernate;
import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.UserRepo;
import org.itrex.util.PasswordEncryption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class UserRepoTest extends TestBaseHibernate {
    private final UserRepo repo;
    private final int usersTableInitialTestSize = 3;

    public UserRepoTest() {
        super();
        repo = new HibernateUserRepo(getSession());
    }

    @Test
    @DisplayName("selectAll with valid data - should have 3 records equal to testdata migration script")
    public void selectAll() {
        // given & when
        List<User> users = repo.selectAll();

        // then
        Assertions.assertEquals(usersTableInitialTestSize, users.size());
        Assertions.assertEquals(1, users.get(0).getUserId());
        Assertions.assertEquals("Andrew", users.get(0).getFirstName());
        Assertions.assertEquals("T", users.get(0).getLastName());
        Assertions.assertEquals("+375293000000", users.get(0).getPhone());
        Assertions.assertEquals("wow@gmail.com", users.get(0).getEmail());
        Assertions.assertEquals(3, users.get(2).getUserId());
        Assertions.assertEquals("+1946484888", users.get(2).getPhone());
    }

    @Test
    @DisplayName("addUser with valid data - users table should contain given user")
    public void addUser1() throws InvalidKeySpecException, NoSuchAlgorithmException {
        // given
        User user = new User();
        user.setPassword(PasswordEncryption.getEncryptedPassword("notSoStrongPassword"));
        user.setFirstName("Freddy");
        user.setLastName("Krueger");
        user.setPhone("1900909Fred");
        user.setEmail("freshmeat@yahoo.com");

        // when
        repo.addUser(user);

        // then
        long usersCount = getSession().createQuery("FROM User", User.class).getResultList().stream()
                .filter(u -> u.getPhone().equals("1900909Fred")).count();
        Assertions.assertEquals(1, usersCount);
    }

    @Test
    @DisplayName("addUser with invalid data - users table shouldn't contain added users")
    public void addUser2() throws InvalidKeySpecException, NoSuchAlgorithmException {
        // given
        User user1 = new User();
        user1.setPassword(PasswordEncryption.getEncryptedPassword("notSoStrongPassword"));
        user1.setFirstName("Freddy");
        user1.setLastName("Krueger");
        // shouldn't exceed 13 сhars
        user1.setPhone("+375-29-333-33-33");
        user1.setEmail("freshmeat@yahoo.com");

        User user2 = new User();
        user2.setPassword(PasswordEncryption.getEncryptedPassword("notSoStrongPassword"));
        user2.setFirstName("Edward");
        user2.setLastName("Scissorshands");
        // should be unique
        user2.setPhone("+375293000000");
        user2.setEmail("holdme@yahoo.com");

        // when
        repo.addUser(user1);
        repo.addUser(user2);

        // then
        Assertions.assertEquals(usersTableInitialTestSize,
                getSession().createQuery("FROM User", User.class).getResultList().size());
    }

    @Test
    @DisplayName("changeEmail with valid data - 'e_mail' field should be changed for a user")
    public void changeEmail() {
        // given
        User user = getSession().find(User.class, 1L);
        String newEmail = "my_new_email@mail.ru";

        // when
        repo.changeEmail(user, newEmail);

        // then
        Assertions.assertEquals(newEmail, user.getEmail());
        Assertions.assertEquals(newEmail, getSession().get(User.class, 1L).getEmail());
    }

    @Test
    @DisplayName("changeDiscount with valid data - 'discount' field should be changed for a user")
    public void changeDiscount() {
        // given
        User user = getSession().find(User.class, 1L);
        Discount newDiscount = Discount.TEN;

        // when
        repo.changeDiscount(user, newDiscount);

        // then
        Assertions.assertEquals(newDiscount, user.getDiscount());
        Assertions.assertEquals(newDiscount, getSession().get(User.class, 1L).getDiscount());
    }

    @Test
    @DisplayName("deleteUser with valid data - should delete user; users table shouldn't contain user; " +
            "all records for user should be deleted")
    public void deleteUser() {
        // given
        Long userId = 1L;
        User user = getSession().get(User.class, userId); // this user has 2 records

        // when
        repo.deleteUser(user);

        // then
        Assertions.assertNull(getSession().get(User.class, userId));

        Query<Record> query = getSession().createQuery("FROM Record WHERE user_id=:userId", Record.class);
        query.setParameter("userId", userId);
        Assertions.assertEquals(0, query.getResultList().size());
    }

    @Test
    @DisplayName("addRoleForUser with valid data - should add 1 row into ManyToMany join table")
    public void addRoleForUser() {
        // given
        Role client = getSession().get(Role.class, 3L); // 2 users have this role
        User user = getSession().get(User.class, 1L); // this user have 2 roles, doesn't have "client" role

        // when
        repo.addRoleForUser(user, client);

        // then
        Assertions.assertTrue(user.getUserRoles().contains(client));
        Assertions.assertTrue(client.getUsers().contains(user));
        Assertions.assertEquals(3, getSession().get(Role.class, 3L).getUsers().size());
        Assertions.assertEquals(3, getSession().get(User.class, 1L).getUserRoles().size());
        Assertions.assertEquals(5, getSession().createSQLQuery("SELECT * FROM users_roles;")
                .getResultList().size());
    }
}
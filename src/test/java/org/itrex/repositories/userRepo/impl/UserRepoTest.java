package org.itrex.repositories.userRepo.impl;

import org.itrex.RepositoryTestBaseHibernate;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.userRepo.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class UserRepoTest extends RepositoryTestBaseHibernate {
    private final UserRepo repo;
    private final int usersTableInitialTestSize = 3;

    public UserRepoTest() {
        super();
        repo = new HibernateUserRepo(getSession());
    }

    @Test
    @DisplayName("selectAll with valid data - should have 3 records")
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
    public void addUser1() {
        // given
        User user1 = new User();
        user1.setFirstName("Freddy");
        user1.setLastName("Krueger");
        user1.setPhone("1900909Fred");
        user1.setEmail("freshmeat@yahoo.com");

        // when
        List<User> usersBeforeAdding = repo.selectAll();
        repo.addUser(user1);
        List<User> usersAfterAdding = repo.selectAll();

        // then
        Assertions.assertEquals(usersTableInitialTestSize, usersBeforeAdding.size());
        Assertions.assertEquals(usersTableInitialTestSize + 1, usersAfterAdding.size());
        Assertions.assertEquals(1, usersAfterAdding.stream()
                .filter(u -> u.getFirstName().equals(user1.getFirstName()))
                .filter(u -> u.getLastName().equals(user1.getLastName()))
                .filter(u -> u.getPhone().equals(user1.getPhone()))
                .filter(u -> u.getEmail().equals(user1.getEmail()))
                .count());
    }

    @Test
    @DisplayName("addUser with invalid data - users table shouldn't contain added users")
    public void addUser2() {
        // given
        User user1 = new User();
        user1.setFirstName("Freddy");
        user1.setLastName("Krueger");
        // shouldn't exceed 13 сhars
        user1.setPhone("+375-29-333-33-33");
        user1.setEmail("freshmeat@yahoo.com");


        User user2 = new User();
        user2.setFirstName("Edward");
        user2.setLastName("Scissorshands");
        // should be unique
        user2.setPhone("+375293000000");
        user2.setEmail("holdme@yahoo.com");

        // when
        List<User> usersBeforeAdding = repo.selectAll();
        try {
            repo.addUser(user1);
            repo.addUser(user2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<User> usersAfterAdding = repo.selectAll();

        // then
        Assertions.assertEquals(usersTableInitialTestSize, usersBeforeAdding.size());
        Assertions.assertEquals(usersTableInitialTestSize, usersAfterAdding.size());
    }

    @Test
    @DisplayName("addAllUser with valid data - users table should contain added users from the list")
    public void addUserList() {
        // given
        User user1 = new User();
        user1.setFirstName("Freddy");
        user1.setLastName("Krueger");
        user1.setPhone("+375293333333");
        user1.setEmail("freshmeat@yahoo.com");

        User user2 = new User();
        user2.setFirstName("Edward");
        user2.setLastName("Scissorshands");
        user2.setPhone("18000001111");
        user2.setEmail("holdme@yahoo.com");

        List<User> listToAdd = Arrays.asList(user1, user2);

        // when
        List<User> usersBeforeAdding = repo.selectAll();
        repo.addUserList(listToAdd);
        List<User> usersAfterAdding = repo.selectAll();

        // then
        Assertions.assertEquals(usersTableInitialTestSize, usersBeforeAdding.size());
        Assertions.assertEquals(usersTableInitialTestSize + listToAdd.size(), usersAfterAdding.size());
        Assertions.assertEquals(1, usersAfterAdding.stream()
                .filter(u -> u.getPhone().equals(user1.getPhone()))
                .count());
        Assertions.assertEquals(1, usersAfterAdding.stream()
                .filter(u -> u.getPhone().equals(user2.getPhone()))
                .count());
    }

    @Test
    @DisplayName("changeEmail with valid data - 'e_mail' field should be changed for a user")
    public void changeEmail() {
        // given
        User user1 = getSession().find(User.class, 1L);
        String newEmail = "my_new_email@mail.ru";

        // when
        repo.changeEmail(user1, newEmail);
        List<User> users = repo.selectAll();

        // then
        Assertions.assertEquals(newEmail, user1.getEmail());
    }

    @Test
    @DisplayName("changeDiscount with valid data - 'discount' field should be changed for a user")
    public void changeDiscount() {
        // given
        User user1 = getSession().find(User.class, 1L);
        Discount newDiscount = Discount.TEN;

        // when
        repo.changeDiscount(user1, newDiscount);
        List<User> users = repo.selectAll();

        // then
        Assertions.assertEquals(newDiscount, user1.getDiscount());
    }
}

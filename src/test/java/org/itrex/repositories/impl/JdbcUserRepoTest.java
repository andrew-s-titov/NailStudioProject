package org.itrex.repositories.impl;

import org.itrex.RepositoryTestBaseJDBC;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RoleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class JdbcUserRepoTest extends RepositoryTestBaseJDBC {
    private final JdbcUserRepo repo;
    private static final String SELECT_ALL_USERS_QUERY = "SELECT * FROM users";

    public JdbcUserRepoTest() {
        super();
        repo = new JdbcUserRepo(getConnectionPool());
    }

    @Test
    @DisplayName("selectAll with valid data - should have 3 records")
    public void selectAll() {
        // given & when
        List<User> users = repo.selectAll(); // should contain 3 users

        // then
        Assertions.assertEquals(3, users.size());
        Assertions.assertEquals(1, users.get(0).getUserId());
        Assertions.assertEquals("Andrew", users.get(0).getFirstName());
        Assertions.assertEquals("T", users.get(0).getLastName());
        Assertions.assertEquals("+375293000000", users.get(0).getPhone());
        Assertions.assertEquals("wow@gmail.com", users.get(0).getEmail());
        Assertions.assertEquals(2, users.get(0).getUserRoles().size());
        Assertions.assertEquals(1, users.get(1).getUserRoles().size());
        Assertions.assertEquals(2, users.get(0).getRecords().size());
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
        repo.addUser(user1);

        // then
        Assertions.assertNotEquals(0, user1.getUserId());

        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(SELECT_ALL_USERS_QUERY)) {

            rs.last();
            Assertions.assertEquals("Freddy", rs.getString("first_name"));
            Assertions.assertEquals("1900909Fred", rs.getString("phone"));

        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
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
        user2.setFirstName("Freddy");
        user2.setLastName("Krueger");
        // should be unique
        user2.setPhone("+375293000000");
        user2.setEmail("freshmeat@yahoo.com");

        // when
        repo.addUser(user1);
        repo.addUser(user2);

        // then
        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(SELECT_ALL_USERS_QUERY)) {

            rs.last();
            Assertions.assertEquals(3, rs.getLong("user_id"));
        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("changeEmail with valid data - 'e_mail' field should be changed for a user")
    public void changeEmail() {
        // given
        User user1 = new User();
        user1.setUserId(1);
        user1.setFirstName("Andrew");
        user1.setLastName("T");
        user1.setPhone("+375293000000");
        user1.setEmail("wow@gmail.com");
        String newEmail = "mynewemal@mail.ru";

        // when
        repo.changeEmail(user1, newEmail);

        // then
        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(SELECT_ALL_USERS_QUERY)) {

            rs.first();
            Assertions.assertEquals(newEmail, rs.getString("e_mail"));

        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("changeDiscount with valid data - 'discount' field should be changed for a user")
    public void changeDiscount() {
        // given
        User user = new User();
        user.setUserId(1);
        user.setFirstName("Andrew");
        user.setLastName("T");
        user.setPhone("+375293000000");
        user.setEmail("wow@gmail.com");
        Discount newDiscount = Discount.TEN;

        // when
        repo.changeDiscount(user, newDiscount);

        // then
        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(SELECT_ALL_USERS_QUERY)) {

            rs.first();
            Assertions.assertEquals(newDiscount.name(), rs.getString("discount"));
        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("deleteUser with valid data - should delete user, all records for him and entries from many-to-many " +
            "join users_roles table")
    public void deleteUser() {
        // given
        long userId = 1;
        User user = new User();
        user.setUserId(userId);
        user.setFirstName("Andrew");
        user.setLastName("T");
        user.setPhone("+375293000000");
        user.setEmail("wow@gmail.com");

        // when
        repo.deleteUser(user);

        // then
        String selectUsersQuery = "SELECT * FROM users WHERE user_id = " + userId;
        String selectRecordsQuery = "SELECT * FROM records WHERE user_id = " + userId;
        String selectRolesQuery = "SELECT * FROM users_roles WHERE user_id = " + userId;
        try (Connection con = getConnectionPool().getConnection();
             Statement stm1 = con.createStatement();
             Statement stm2 = con.createStatement();
             Statement stm3 = con.createStatement()) {

            ResultSet rs1 = stm1.executeQuery(selectUsersQuery);
            ResultSet rs2 = stm2.executeQuery(selectRecordsQuery);
            ResultSet rs3 = stm3.executeQuery(selectRolesQuery);

            Assertions.assertFalse(rs1.next());
            Assertions.assertFalse(rs2.next());
            Assertions.assertFalse(rs3.next());

        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("addRole with valid data - should insert entry into many-to-many join table")
    public void addRole() {
        // given
        long userId = 1;
        User user = new User();
        user.setUserId(userId);
        user.setFirstName("Andrew");
        user.setLastName("T");
        user.setPhone("+375293000000");
        user.setEmail("wow@gmail.com");

        Role role1 = new Role();
        role1.setRoleId(1);
        role1.setRoletype(RoleType.ADMIN);
        user.getUserRoles().add(role1);

        Role role2 = new Role();
        role2.setRoleId(2);
        role2.setRoletype(RoleType.MASTER);
        user.getUserRoles().add(role2);

        Role role3 = new Role();
        role3.setRoleId(3);
        role3.setRoletype(RoleType.CLIENT);

        // when
        repo.addRole(user, role3);

        // then
        Assertions.assertTrue(role3.getUsers().contains(user));
        Assertions.assertTrue(user.getUserRoles().contains(role3));

        String selectCountRolesQuery = "SELECT COUNT(*) AS count FROM users_roles WHERE user_id = 1";
        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(selectCountRolesQuery)) {

            rs.first();
            Assertions.assertEquals(3, rs.getInt("count"));

        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }
}
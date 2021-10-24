package org.itrex.repositories.impl;

import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RecordTime;
import org.itrex.entities.enums.RoleType;
import org.itrex.repositories.UserRepo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JdbcUserRepo implements UserRepo {
    private static final String TABLE_NAME = "users";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String FIRSTNAME_COLUMN = "first_name";
    private static final String LASTNAME_COLUMN = "last_name";
    private static final String PHONE_COLUMN = "phone";
    private static final String EMAIL_COLUMN = "e_mail";
    private static final String DISCOUNT_COLUMN = "discount";

    private final DataSource connectionSource;

    public JdbcUserRepo(DataSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    private void insertUser(User user, Connection con) throws SQLException {
        String insertUserQuery = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                TABLE_NAME, FIRSTNAME_COLUMN, LASTNAME_COLUMN, PHONE_COLUMN, EMAIL_COLUMN);
        PreparedStatement stm = con.prepareStatement(insertUserQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        stm.setString(1, user.getFirstName());
        stm.setString(2, user.getLastName());
        stm.setString(3, user.getPhone());
        stm.setString(4, user.getEmail());

        int affectedRows = stm.executeUpdate();

        if (affectedRows == 1) {
            ResultSet keys = stm.getGeneratedKeys();
            if (keys.next()) {
                user.setUserId(keys.getInt(USER_ID_COLUMN));
            }
        }

        for (Role role : user.getUserRoles()) {
            long roleId = role.getRoleId();
            String insertRoleQuery = String.format("INSERT INTO users_roles VALUES (%s, %s)", user.getUserId(), roleId);
            con.createStatement().executeUpdate(insertRoleQuery);
        }
    }

    @Override
    public List<User> selectAll() {
        List<User> users = new ArrayList<>();
        String selectAllQuery = String.format("SELECT * FROM %s", TABLE_NAME);

        try (Connection con = connectionSource.getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(selectAllQuery)) {
            while (rs.next()) {
                User user = new User();
                long userId = rs.getLong(USER_ID_COLUMN);
                user.setUserId(rs.getLong(USER_ID_COLUMN));
                user.setFirstName(rs.getString(FIRSTNAME_COLUMN));
                user.setLastName(rs.getString(LASTNAME_COLUMN));
                user.setPhone(rs.getString(PHONE_COLUMN));
                user.setEmail(rs.getString(EMAIL_COLUMN));
                user.setDiscount(Discount.valueOf(rs.getString(DISCOUNT_COLUMN)));

                String rolesQuery =
                        "SELECT * FROM roles r LEFT JOIN users_roles j ON r.role_id = j.role_id WHERE j.user_id = "
                                + userId;
                try (Statement stm2 = con.createStatement(); ResultSet rsRoles = stm2.executeQuery(rolesQuery)) {

                    Set<Role> roles = user.getUserRoles();
                    while (rsRoles.next()) {
                        Role role = new Role();
                        role.setRoleId(rsRoles.getLong("role_id"));
                        role.setRoletype(RoleType.valueOf(rsRoles.getString("role_name")));
                        roles.add(role);
                    }
                } catch (SQLException ex) {
                    // TODO: exception handling (logging)
                    ex.printStackTrace();
                }

                String recordsQuery = "SELECT * FROM records WHERE user_id = " + userId;
                try (Statement stm3 = con.createStatement(); ResultSet rsRecords = stm3.executeQuery(recordsQuery)) {
                    while (rsRecords.next()) {
                        Record record = new Record();
                        record.setRecordId(rsRecords.getLong("record_id"));
                        record.setUser(user);
                        record.setDate(rsRecords.getDate("date"));
                        record.setTime(RecordTime.valueOf(rsRecords.getString("time")));
                    }
                } catch (SQLException ex) {
                    // TODO: exception handling (logging)
                    ex.printStackTrace();
                }

                users.add(user);
            }
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
        return users;
    }

    @Override
    public void addUser(User user) {
        try (Connection con = connectionSource.getConnection()) {
            insertUser(user, con);
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteUser(User user) {
        long userId = user.getUserId();

        try (Connection con = connectionSource.getConnection(); Statement st = con.createStatement()) {

            // deleting records for user
            String deleteRecordsForUser = "DELETE FROM records WHERE user_id = " + userId;
            st.executeUpdate(deleteRecordsForUser);

            // deleting many-to-many entries user-role
            String deleteUserRoles = "DELETE FROM users_roles WHERE user_id = " + userId;
            st.executeUpdate(deleteUserRoles);

            // deleting users
            String deleteUserQuery = String.format("DELETE FROM %s WHERE %s = %s", TABLE_NAME, USER_ID_COLUMN, userId);
            st.executeUpdate(deleteUserQuery);

        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void addRole(User user, Role role) {
        long userId = user.getUserId();
        long roleId = role.getRoleId();

        String addRoleQuery = String.format("INSERT INTO users_roles VALUES (%s, %s)", userId, roleId);
        try (Connection con = connectionSource.getConnection(); Statement st = con.createStatement()) {

            st.executeUpdate(addRoleQuery);
            user.getUserRoles().add(role);
            role.getUsers().add(user);

        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        String changeUserEmailQuery = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
                TABLE_NAME, EMAIL_COLUMN, USER_ID_COLUMN);
        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(changeUserEmailQuery)) {
            stm.setString(1, newEmail);
            stm.setLong(2, user.getUserId());
            stm.executeUpdate();

            user.setEmail(newEmail);
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        String changeUserDiscountQuery = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
                TABLE_NAME, DISCOUNT_COLUMN, USER_ID_COLUMN);
        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(changeUserDiscountQuery)) {
            stm.setString(1, discount.name());
            stm.setLong(2, user.getUserId());
            stm.executeUpdate();

            user.setDiscount(discount);
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }
}
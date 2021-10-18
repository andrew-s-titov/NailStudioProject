package org.itrex.repositories.userRepo.impl;

import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.userRepo.UserRepo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserRepo implements UserRepo {
    private static final String TABLE_NAME = "users";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String FIRSTNAME_COLUMN = "first_name";
    private static final String LASTNAME_COLUMN = "last_name";
    private static final String PHONE_COLUMN = "phone";
    private static final String EMAIL_COLUMN = "e_mail";
    private static final String DISCOUNT_COLUMN = "discount";

    private static final String SELECT_ALL_QUERY = String.format("SELECT * FROM %s", TABLE_NAME);
    private static final String INSERT_USER_QUERY = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
            TABLE_NAME, FIRSTNAME_COLUMN, LASTNAME_COLUMN, PHONE_COLUMN, EMAIL_COLUMN);
    private static final String CHANGE_USER_EMAIL_QUERY = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
            TABLE_NAME, EMAIL_COLUMN, USER_ID_COLUMN);
    private static final String CHANGE_USER_DISCOUNT_QUERY = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
            TABLE_NAME, DISCOUNT_COLUMN, USER_ID_COLUMN);
    private static final String DELETE_USER_QUERY = String.format("DELETE FROM %s WHERE %s = ?", TABLE_NAME, USER_ID_COLUMN);

    private final DataSource connectionSource;

    public JdbcUserRepo(DataSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    private void insertUser(User user, Connection con) throws SQLException {
        PreparedStatement stm = con.prepareStatement(INSERT_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
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
    }

    @Override
    public List<User> selectAll() {
        List<User> users = new ArrayList<>();

        try (Connection con = connectionSource.getConnection(); Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(SELECT_ALL_QUERY)) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getLong(USER_ID_COLUMN));
                user.setFirstName(rs.getString(FIRSTNAME_COLUMN));
                user.setLastName(rs.getString(LASTNAME_COLUMN));
                user.setPhone(rs.getString(PHONE_COLUMN));
                user.setEmail(rs.getString(EMAIL_COLUMN));
                user.setDiscount(Discount.valueOf(rs.getString(DISCOUNT_COLUMN)));

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
    public void addUserList(List<User> users) {
        try (Connection con = connectionSource.getConnection()) {
            con.setAutoCommit(false);
            try {
                for (User user : users) {
                    insertUser(user, con);
                }
                con.commit();
            } catch (SQLException ex) {
                // TODO: exception handling (logging)
                ex.printStackTrace();
                con.rollback();
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(CHANGE_USER_EMAIL_QUERY)) {
            stm.setString(1, newEmail);
            stm.setLong(2, user.getUserId());
            stm.executeUpdate();
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(CHANGE_USER_DISCOUNT_QUERY)) {
            stm.setString(1, discount.name());
            stm.setLong(2, user.getUserId());
            stm.executeUpdate();
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }
}
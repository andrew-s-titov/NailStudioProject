package org.itrex.repositories.impl;

import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.RecordRepo;

import java.util.List;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcRecordRepo implements RecordRepo {
    private static final String TABLE_NAME = "records";
    private static final String RECORD_ID_COLUMN = "record_id";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String DATE_COLUMN = "date";
    private static final String TIME_COLUMN = "time";

    private final DataSource connectionSource;

    public JdbcRecordRepo(DataSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override
    public List<Record> selectAll() {
        List<Record> records = new ArrayList<>();

        String selectAllQuery = "SELECT * FROM " + TABLE_NAME;
        try (Connection con = connectionSource.getConnection(); Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(selectAllQuery)) {
            while (rs.next()) {
                long recordId = rs.getLong(RECORD_ID_COLUMN);

                Record record = new Record();
                record.setRecordId(recordId);

                long userId = rs.getLong(USER_ID_COLUMN);
                User user = new User();

                String selectUserQuery = "SELECT * FROM users WHERE user_id = " + userId;
                try (Statement stm2 = con.createStatement();
                     ResultSet rs2 = stm2.executeQuery(selectUserQuery)) {
                    if (rs2.next()) {
                        user.setUserId(rs2.getLong("user_id"));
                        user.setFirstName(rs2.getString("first_name"));
                        user.setLastName(rs2.getString("last_name"));
                        user.setPhone(rs2.getString("phone"));
                        user.setEmail(rs2.getString("e_mail"));
                        user.setDiscount(Discount.valueOf(rs2.getString("discount")));
                    }
                } catch (SQLException ex) {
                    // TODO: exception handling (logging)
                    ex.printStackTrace();
                }

                record.setUser(user);
                record.setDate(rs.getDate(DATE_COLUMN));
                record.setTime(RecordTime.valueOf(rs.getString(TIME_COLUMN)));

                records.add(record);
            }
        } catch (
                SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }

        return records;
    }

    @Override
    public void addRecord(Record record) {
        String insertRecordQuery = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                TABLE_NAME, USER_ID_COLUMN, DATE_COLUMN, TIME_COLUMN);

        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(insertRecordQuery, Statement.RETURN_GENERATED_KEYS)) {
            stm.setLong(1, record.getUser().getUserId());
            stm.setDate(2, record.getDate());
            stm.setString(3, record.getTime().name());

            int affectedRows = stm.executeUpdate();

            if (affectedRows == 1) {
                ResultSet keys = stm.getGeneratedKeys();
                if (keys.next()) {
                    record.setRecordId(keys.getInt(RECORD_ID_COLUMN));
                }
            }
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteRecord(Record record) {
        long recordId = record.getRecordId();
        String deleteRecordQuery = String.format("DELETE FROM %s WHERE %s = %s", TABLE_NAME, RECORD_ID_COLUMN, recordId);
        try (Connection con = connectionSource.getConnection()) {
            con.createStatement().executeUpdate(deleteRecordQuery);
            record.getUser().getRecords().remove(record);
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteRecordsForUser(User user) {
        long userId = user.getUserId();
        String deleteUserRecordQuery = String.format("DELETE FROM %s WHERE %s = %s", TABLE_NAME, USER_ID_COLUMN, userId);
        try (Connection con = connectionSource.getConnection()) {
            con.createStatement().executeUpdate(deleteUserRecordQuery);
            user.getRecords().clear();
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        long recordId = record.getRecordId();
        String updateTimeQuery = String.format("UPDATE %s SET %s = ? WHERE %s = %s",
                TABLE_NAME, TIME_COLUMN, RECORD_ID_COLUMN, recordId);
        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(updateTimeQuery)) {
            stm.setString(1, newTime.name());
            stm.executeUpdate();
            record.setTime(newTime);
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }
}
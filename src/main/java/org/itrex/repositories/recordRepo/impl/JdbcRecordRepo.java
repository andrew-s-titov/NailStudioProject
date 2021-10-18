package org.itrex.repositories.recordRepo.impl;

import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.recordRepo.RecordRepo;

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

    private static final String SELECT_ALL_QUERY = String.format("SELECT * FROM %s", TABLE_NAME);
    private static final String INSERT_RECORD_QUERY = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
            TABLE_NAME, USER_ID_COLUMN, DATE_COLUMN, TIME_COLUMN);
    private static final String DELETE_RECORD_QUERY = String.format("DELETE FROM %s WHERE %s = ?",
            TABLE_NAME, RECORD_ID_COLUMN);
    private static final String DELETE_USER_RECORD_QUERY = String.format("DELETE FROM %s WHERE %s = ?",
            TABLE_NAME, USER_ID_COLUMN);

    private final DataSource connectionSource;

    public JdbcRecordRepo(DataSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override
    public List<Record> selectAll() {
        List<Record> records = new ArrayList<>();

        try (Connection con = connectionSource.getConnection(); Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(SELECT_ALL_QUERY)) {
            while (rs.next()) {
                Record record = new Record();
                record.setRecordId(rs.getLong(RECORD_ID_COLUMN));
                record.setUserId(rs.getLong(USER_ID_COLUMN));
                record.setRecordDate(rs.getDate(DATE_COLUMN));
                record.setRecordTime(RecordTime.valueOf(rs.getString(TIME_COLUMN)));

                records.add(record);
            }
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }

        return records;
    }

    @Override
    public void addRecord(Record record) {
        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(INSERT_RECORD_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            stm.setLong(1, record.getUserId());
            stm.setDate(2, record.getRecordDate());
            stm.setString(3, record.getRecordTime().name());

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
        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(DELETE_RECORD_QUERY)) {
            stm.setLong(1, record.getRecordId());
            stm.executeUpdate();
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteRecordsForUser(User user) {
        try (Connection con = connectionSource.getConnection();
             PreparedStatement stm = con.prepareStatement(DELETE_USER_RECORD_QUERY)) {
            stm.setLong(1, user.getUserId());
            stm.executeUpdate();
        } catch (SQLException ex) {
            // TODO: exception handling (logging)
            ex.printStackTrace();
        }
    }
}

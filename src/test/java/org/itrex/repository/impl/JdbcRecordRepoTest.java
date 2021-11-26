/*package org.itrex.repositories.impl;

import org.itrex.RepositoryTestBaseJDBC;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

public class JdbcRecordRepoTest extends RepositoryTestBaseJDBC {

    private final JdbcRecordRepo repo;
    private final int recordsTableInitialTestSize = 4;

    public JdbcRecordRepoTest() {
        super();
        repo = new JdbcRecordRepo(getConnectionPool());
    }

    @Test
    @DisplayName("selectAll with valid data - should have 2 records")
    public void selectAll() {
        // given & when
        List<Record> records = repo.selectAll();

        // then
        Assertions.assertEquals(recordsTableInitialTestSize, records.size());
        Assertions.assertEquals(1, records.get(0).getUser().getUserId());
        Assertions.assertEquals(Date.valueOf("2021-10-18"), records.get(0).getDate());
        Assertions.assertEquals(RecordTime.NINE, records.get(0).getTime());
        Assertions.assertEquals(3, records.get(2).getUser().getUserId());
        Assertions.assertEquals(RecordTime.THIRTEEN, records.get(2).getTime());
    }

    @Test
    @DisplayName("addRecord with valid data - records table should contain added record")
    public void addRecord() {
        // given
        User user = new User();
        user.setUserId(1);
        user.setFirstName("Andrew");
        user.setLastName("T");
        user.setPhone("+375293000000");
        user.setEmail("wow@gmail.com");

        Record record = new Record();
        record.setUser(user);
        record.setDate(Date.valueOf("2021-12-19"));
        record.setTime(RecordTime.NINE);

        // when
        repo.addRecord(record);

        // then
        Assertions.assertTrue(user.getRecords().contains(record));
        Assertions.assertEquals(user, record.getUser());

        String selectAllQuery = "SELECT * FROM records";
        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(selectAllQuery)) {

            Assertions.assertTrue(rs.last());
            Assertions.assertEquals(1, rs.getLong("user_id"));
            Assertions.assertEquals(Date.valueOf("2021-12-19"), rs.getDate("date"));
            Assertions.assertEquals(RecordTime.NINE.name(), rs.getString("time"));

        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain deleted record")
    public void deleteRecord() {
        // given
        User user = new User();
        user.setUserId(1);
        user.setFirstName("Andrew");
        user.setLastName("T");
        user.setPhone("+375293000000");
        user.setEmail("wow@gmail.com");

        Record record = new Record(); // existing record
        record.setRecordId(1);
        record.setUser(user);
        record.setDate(Date.valueOf("2021-10-18"));
        record.setTime(RecordTime.NINE);

        // when
        repo.deleteRecord(record);

        // then
        Assertions.assertFalse(user.getRecords().contains(record));

        String selectAllQuery = "SELECT * FROM records WHERE record_id = 1";
        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(selectAllQuery)) {

            Assertions.assertFalse(rs.next());
        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("deleteRecordForUser with valid data - records table shouldn't contain records for the user")
    public void deleteRecordsForUser() {
        // given
        User user = new User();
        user.setUserId(1);
        user.setFirstName("Andrew");
        user.setLastName("T");
        user.setPhone("+375293000000");
        user.setEmail("wow@gmail.com");

        // when
        repo.deleteRecordsForUser(user);

        // then
        Assertions.assertTrue(user.getRecords().isEmpty());
        String selectAllQuery = "SELECT * FROM records WHERE user_id = 1";
        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(selectAllQuery)) {

            Assertions.assertFalse(rs.next());
        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("changeRecordTime with valid data - should change time for this record")
    public void changeRecordTime() {
        // given
        Record record = new Record(); // existing record
        record.setRecordId(1);
        record.setDate(Date.valueOf("2021-10-18"));
        record.setTime(RecordTime.NINE);
        RecordTime newTime = RecordTime.THIRTEEN;

        //when
        repo.changeRecordTime(record, newTime);

        //then
        Assertions.assertEquals(newTime, record.getTime());
        String selectAllQuery = "SELECT * FROM records WHERE user_id = 1";
        try (Connection con = getConnectionPool().getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(selectAllQuery)) {

            Assertions.assertTrue(rs.next());
            Assertions.assertEquals(newTime.name(), rs.getString("time"));
        } catch (SQLException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

}*/

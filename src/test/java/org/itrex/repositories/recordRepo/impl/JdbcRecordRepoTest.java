package org.itrex.repositories.recordRepo.impl;

import org.itrex.RepositoryTestBase;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

public class JdbcRecordRepoTest extends RepositoryTestBase {
    private final JdbcRecordRepo repo;
    private final int recordsTableInitialTestSize = 2;

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
        Assertions.assertEquals(1, records.get(0).getUserId());
        Assertions.assertEquals(Date.valueOf("2021-10-18"), records.get(0).getRecordDate());
        Assertions.assertEquals(RecordTime.NINE, records.get(0).getRecordTime());
        Assertions.assertEquals(3, records.get(1).getUserId());
        Assertions.assertEquals(RecordTime.THIRTEEN, records.get(1).getRecordTime());
    }

    @Test
    @DisplayName("addRecord with valid data - records table should contain added record")
    public void addRecord() {
        // given
        Record record1 = new Record();
        record1.setUserId(2);
        record1.setRecordDate(Date.valueOf("2021-10-19"));
        record1.setRecordTime(RecordTime.NINE);

        // when
        List<Record> recordsBeforeAdding = repo.selectAll();
        repo.addRecord(record1);
        List<Record> records = repo.selectAll();

        // then
        Assertions.assertEquals(recordsTableInitialTestSize, recordsBeforeAdding.size());
        Assertions.assertEquals(recordsTableInitialTestSize + 1, records.size());
        Assertions.assertEquals(1, records.stream()
                .filter(r -> r.getUserId() == record1.getUserId())
                .filter(r -> r.getRecordDate().equals(record1.getRecordDate()))
                .filter(r -> r.getRecordTime().equals(record1.getRecordTime()))
                .count());
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain deleted record")
    public void deleteRecord() {
        // given
        Record record1 = new Record();
        record1.setRecordId(1);
        record1.setUserId(1);
        record1.setRecordDate(Date.valueOf("2021-10-18"));
        record1.setRecordTime(RecordTime.NINE);

        // when
        List<Record> recordsBeforeDeleting = repo.selectAll();
        repo.deleteRecord(record1);
        List<Record> recordsAfterDeleting = repo.selectAll();

        // then
        Assertions.assertEquals(recordsTableInitialTestSize, recordsBeforeDeleting.size());
        Assertions.assertEquals(recordsTableInitialTestSize - 1, recordsAfterDeleting.size());
        Assertions.assertEquals(0, recordsAfterDeleting.stream()
                .filter(r -> r.getRecordId() == record1.getRecordId())
                .count());
    }

    @Test
    @DisplayName("deleteRecordForUser with valid data - records table shouldn't contain records for the user")
    public void deleteRecordsForUser() {
        // given
        User user1 = new User();
        user1.setUserId(1);
        user1.setFirstName("Andrew");
        user1.setLastName("T");
        user1.setPhone("+375293000000");
        user1.setEmail("wow@gmail.com");

        // when
        List<Record> recordsBeforeDeleting = repo.selectAll();
        repo.deleteRecordsForUser(user1);
        List<Record> recordsAfterDeleting = repo.selectAll();

        // then
        Assertions.assertEquals(recordsTableInitialTestSize, recordsBeforeDeleting.size());
        Assertions.assertEquals(recordsTableInitialTestSize - 1, recordsAfterDeleting.size());
        Assertions.assertEquals(0, recordsAfterDeleting.stream()
                        .filter(r -> r.getUserId() == user1.getUserId())
                        .count());
    }
}
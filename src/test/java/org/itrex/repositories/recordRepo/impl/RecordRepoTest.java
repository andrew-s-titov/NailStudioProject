package org.itrex.repositories.recordRepo.impl;

import org.itrex.RepositoryTestBaseHibernate;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.recordRepo.RecordRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

public class RecordRepoTest extends RepositoryTestBaseHibernate {
    private final RecordRepo repo;
    private final int recordsTableInitialTestSize = 4;

    public RecordRepoTest() {
        super();
        repo = new HibernateRecordRepo(getSession());
    }

    @Test
    @DisplayName("selectAll with valid data - should have 4 records")
    public void selectAll() {
        // given & when
        List<Record> records = repo.selectAll();

        // then
        Assertions.assertEquals(recordsTableInitialTestSize, records.size());
        Assertions.assertEquals(1, records.get(0).getUser().getUserId());
        Assertions.assertEquals(Date.valueOf("2021-10-18"), records.get(0).getDate());
        Assertions.assertEquals(RecordTime.NINE, records.get(0).getTime());
        Assertions.assertEquals(2, records.get(3).getUser().getUserId());
        Assertions.assertEquals(RecordTime.SEVENTEEN, records.get(3).getTime());
    }

    @Test
    @DisplayName("addRecord with valid data - records table should contain added record")
    public void addRecord() {
        // given
        long userId = 1L;
        User user1 = getSession().find(User.class, userId);
        Record record1 = new Record();
        record1.setUser(user1);
        record1.setDate(Date.valueOf("2021-10-19"));
        record1.setTime(RecordTime.NINE);

        // when
        List<Record> recordsBeforeAdding = repo.selectAll();
        repo.addRecord(record1);
        List<Record> records = repo.selectAll();

        // then
        Assertions.assertEquals(recordsTableInitialTestSize, recordsBeforeAdding.size());
        Assertions.assertEquals(recordsTableInitialTestSize + 1, records.size());
        Assertions.assertEquals(1, records.stream()
                .filter(r -> r.getUser().getUserId() == userId)
                .filter(r -> r.getDate().equals(record1.getDate()))
                .filter(r -> r.getTime().equals(record1.getTime()))
                .count());
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain deleted record")
    public void deleteRecord() {
        // given
        List<Record> recordsBeforeDeleting = repo.selectAll();
        Record record1 = recordsBeforeDeleting.get(0);

        // when
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
        long userId = 1L;
        // this user has 2 records
        User user1 = getSession().find(User.class, userId);

        // when
        List<Record> recordsBeforeDeleting = repo.selectAll();
        repo.deleteRecordsForUser(user1);
        List<Record> recordsAfterDeleting = repo.selectAll();

        // then
        Assertions.assertEquals(recordsTableInitialTestSize, recordsBeforeDeleting.size());
        Assertions.assertEquals(recordsTableInitialTestSize - 2, recordsAfterDeleting.size());
        Assertions.assertEquals(0, recordsAfterDeleting.stream()
                        .filter(r -> r.getUser().getUserId() == userId)
                        .count());
    }
}
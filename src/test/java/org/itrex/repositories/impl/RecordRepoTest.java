package org.itrex.repositories.impl;

import org.itrex.RepositoryTestBaseHibernate;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.RecordRepo;
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
        User user1 = getSession().get(User.class, userId);
        int recordsCount = user1.getRecords().size();

        Record record1 = new Record();
        record1.setUser(user1);
        record1.setDate(Date.valueOf("2021-10-19"));
        record1.setTime(RecordTime.NINE);

        // when
        repo.addRecord(record1);

        // then
        Assertions.assertEquals(recordsTableInitialTestSize + 1,
                getSession().createQuery("FROM Record", Record.class).getResultList().size());
        Assertions.assertEquals(recordsCount + 1, getSession().get(User.class, userId).getRecords().size());
    }

    @Test
    @DisplayName("changeRecordTime with valid data - records table should update time for this record")
    public void changeRecordTime() {
        // given
        Record record = getSession().get(Record.class, 1L); // time 09:00 'NINE'
        RecordTime newTime = RecordTime.SEVENTEEN;
        long userId = record.getUser().getUserId();

        // when
        repo.changeRecordTime(record, RecordTime.SEVENTEEN);

        // then
        Assertions.assertEquals(newTime, record.getTime());
        Assertions.assertEquals(newTime, getSession().get(Record.class, 1L).getTime());
        RecordTime time = getSession().get(User.class, userId).getRecords().stream()
                .filter(r -> r.getRecordId() == 1L)
                .findAny()
                .get()
                .getTime();
        Assertions.assertEquals(newTime, time);
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain deleted record," +
            "method shouldn't delete user with this record")
    public void deleteRecord() {
        // given
        Record record = getSession().get(Record.class, 1L);
        long userId = record.getUser().getUserId();
        int recordsCount = getSession().get(User.class, userId).getRecords().size();

        // when
        repo.deleteRecord(record);

        // then
        Assertions.assertNull(getSession().get(Record.class, 1L));
        Assertions.assertNotNull(getSession().get(User.class, userId));
        Assertions.assertEquals(recordsCount - 1, getSession().get(User.class, userId).getRecords().size());
    }

    @Test
    @DisplayName("deleteRecordForUser with valid data - records table shouldn't contain records for the user," +
            "method shouldn't delete user with these records")
    public void deleteRecordsForUser() {
        // given
        long userId = 1L;
        User user1 = getSession().get(User.class, userId); // this user has 2 records

        // when
        repo.deleteRecordsForUser(user1);

        // then
        Assertions.assertEquals(0, getSession().createQuery("FROM Record WHERE user_id=:id")
        .setParameter("id", userId).getResultList().size());
        Assertions.assertEquals(0, getSession().get(User.class, userId).getRecords().size());
        Assertions.assertNotNull(getSession().get(User.class, userId));
    }
}
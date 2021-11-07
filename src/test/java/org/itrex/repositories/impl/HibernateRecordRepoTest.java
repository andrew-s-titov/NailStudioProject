package org.itrex.repositories.impl;

import org.itrex.TestBaseHibernate;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

public class HibernateRecordRepoTest extends TestBaseHibernate {
    private final HibernateRecordRepo repo;
    private final int recordsTableInitialTestSize = 4;

    public HibernateRecordRepoTest() {
        repo = getContext().getBean(HibernateRecordRepo.class);
    }

    @Test
    @DisplayName("selectAll with valid data - should have 4 records equal to testdata migration script")
    public void selectAll() {
        // given & when
        List<Record> records = repo.getAll();

        // then
        Assertions.assertEquals(recordsTableInitialTestSize, records.size());
        Assertions.assertEquals(1, records.get(0).getUser().getUserId());
        Assertions.assertEquals(Date.valueOf("2021-10-18"), records.get(0).getDate());
        Assertions.assertEquals(RecordTime.NINE, records.get(0).getTime());
        Assertions.assertEquals(2, records.get(3).getUser().getUserId());
        Assertions.assertEquals(RecordTime.SEVENTEEN, records.get(3).getTime());
        repo.closeRepoSession();
    }

    @Test
    @DisplayName("addRecord with valid data - records table should contain added Record")
    public void addRecord() {
        // given
        long userId = 1L;
        User user = repo.getCurrentSession().get(User.class, userId);
        int recordsCount = user.getRecords().size();

        Record record1 = new Record();
        record1.setUser(user);
        record1.setDate(Date.valueOf("2021-10-19"));
        record1.setTime(RecordTime.NINE);

        // when
        repo.addRecord(record1);

        // then
        Assertions.assertEquals(recordsTableInitialTestSize + 1,
                repo.getCurrentSession().createQuery("FROM Record", Record.class).getResultList().size());
        Assertions.assertEquals(recordsCount + 1, repo.getCurrentSession().get(User.class, userId).getRecords().size());
        repo.closeRepoSession();
    }

    @Test
    @DisplayName("changeRecordTime with valid data - records table should update time for this record")
    public void changeRecordTime() {
        // given
        Record record = repo.getCurrentSession().get(Record.class, 1L); // time 09:00 'NINE'
        RecordTime newTime = RecordTime.SEVENTEEN;
        long userId = record.getUser().getUserId();

        // when
        repo.changeRecordTime(record, RecordTime.SEVENTEEN);

        // then
        Assertions.assertEquals(newTime, record.getTime());
        Assertions.assertEquals(newTime, repo.getCurrentSession().get(Record.class, 1L).getTime());
        RecordTime time = repo.getCurrentSession().get(User.class, userId).getRecords().stream()
                .filter(r -> r.getRecordId() == 1L)
                .findAny()
                .get()
                .getTime();
        Assertions.assertEquals(newTime, time);
        repo.closeRepoSession();
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain deleted Record;" +
            "method shouldn't delete user with this record")
    public void deleteRecord() {
        // given
        Record record = repo.getCurrentSession().get(Record.class, 1L);
        long userId = record.getUser().getUserId();
        int recordsCount = repo.getCurrentSession().get(User.class, userId).getRecords().size();

        // when
        repo.deleteRecord(record);

        // then
        Assertions.assertNull(repo.getCurrentSession().get(Record.class, 1L));
        Assertions.assertNotNull(repo.getCurrentSession().get(User.class, userId));
        Assertions.assertEquals(recordsCount - 1, repo.getCurrentSession().get(User.class, userId).getRecords().size());
        repo.closeRepoSession();
    }

    @Test
    @DisplayName("deleteRecordForUser with valid data - records table shouldn't contain records for the user," +
            "method shouldn't delete user with these records")
    public void deleteRecordsForUser() {
        // given
        long userId = 1L;
        User user = repo.getCurrentSession().get(User.class, userId); // this user has 2 records

        // when
        repo.deleteRecordsForUser(user);

        // then
        Assertions.assertEquals(0, repo.getCurrentSession().createQuery("FROM Record WHERE user_id=:id")
        .setParameter("id", userId).getResultList().size());
        Assertions.assertEquals(0, repo.getCurrentSession().get(User.class, userId).getRecords().size());
        Assertions.assertEquals(0, user.getRecords().size());
        Assertions.assertNotNull(repo.getCurrentSession().get(User.class, userId));
        repo.closeRepoSession();
    }
}
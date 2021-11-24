package org.itrex.repositories.impl;

import org.hibernate.Session;
import org.itrex.TestBaseHibernate;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.exceptions.DatabaseEntryNotFoundException;
import org.itrex.repositories.RecordRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateRecordRepoTest extends TestBaseHibernate {
    @Autowired
    private RecordRepo repo;
    private final int recordsTableInitialTestSize = 4;
    private Session session;

    @Test
    @DisplayName("getAll - should return 4 Records equal to testdata migration script")
    public void getAll() {
        // given & when
        List<Record> records = repo.getAll();

        // then
        assertEquals(recordsTableInitialTestSize, records.size());
        assertEquals(1, records.get(0).getUser().getUserId());
        assertEquals(Date.valueOf("2021-10-18"), records.get(0).getDate());
        assertEquals(RecordTime.NINE, records.get(0).getTime());
        assertEquals(2, records.get(3).getUser().getUserId());
        assertEquals(RecordTime.SEVENTEEN, records.get(3).getTime());
    }

    @Test
    @DisplayName("getRecordById with valid data - should return existing Record")
    public void getRecordById1() {
        // given & when
        Record record1 = repo.getRecordById(1L);
        Record record2 = repo.getRecordById(3L);

        // then
        assertEquals(1, record1.getRecordId());
        assertEquals(RecordTime.NINE, record1.getTime());
        assertEquals(Date.valueOf("2021-10-18"), record1.getDate());
        assertEquals(3, record2.getRecordId());
        assertEquals(RecordTime.THIRTEEN, record2.getTime());
        assertEquals(Date.valueOf("2021-10-18"), record2.getDate());
    }

    @Test
    @DisplayName("getRecordById with invalid data - should throw DatabaseEntryNotFoundException")
    public void getRecordById2() {
        // given
        long recordId1 = 7L;
        long recordId2 = 180L;

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> repo.getRecordById(recordId1));
        assertThrows(DatabaseEntryNotFoundException.class, () -> repo.getRecordById(recordId2));
    }

    @Test
    @DisplayName("getRecordsForUserByUserId with valid data - should return a list of Records for User")
    public void getRecordsForUserByUserId1() {
        // given & when
        long userId = 1L; // User with this id has 2 records
        List<Record> records = repo.getRecordsForUser(userId);

        // then
        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("getRecordsForUserByUserId with invalid data - should return empty list")
    public void getRecordsForUserByUserId2() {
        // given
        long userId = 7L; // there are no Users with this id
        List<Record> records = repo.getRecordsForUser(userId);

        // when & then
        assertEquals(0, records.size());
    }

    @Test
    @DisplayName("addRecordForUser with valid data - records table should contain added Record")
    public void addRecordForUser() {
        // given
        session = getSessionFactory().openSession();
        long userId = 1L; // this User has 2 records
        User user = session.find(User.class, userId);
        int recordsCount = user.getRecords().size();
        session.close();

        Record record = Record.builder()
                .date(Date.valueOf("2021-10-19"))
                .time(RecordTime.NINE)
                .build();

        // when
        repo.createRecordForClient(user, record);

        // then
        session = getSessionFactory().openSession();
        assertEquals(recordsTableInitialTestSize + 1,
                session.createQuery("FROM Record", Record.class).list().size());
        assertEquals(recordsCount + 1, session.get(User.class, userId).getRecords().size());
        session.close();
    }

    @Test
    @DisplayName("changeRecordTime with valid data - records table should update time for this record")
    public void changeRecordTime() {
        // given
        session = getSessionFactory().openSession();
        Record record = session.find(Record.class, 1L); // time 09:00 'NINE'
        RecordTime newTime = RecordTime.SEVENTEEN;
        long userId = record.getUser().getUserId();
        session.close();

        // when
        repo.changeRecordTime(record, newTime);
        session = getSessionFactory().openSession();
        record = session.find(Record.class, 1L);
        session.close();

        // then
        assertEquals(newTime, record.getTime());
        session = getSessionFactory().openSession();
        RecordTime time = session.find(User.class, userId).getRecords().stream()
                .filter(r -> r.getRecordId() == 1L)
                .findAny()
                .get()
                .getTime();
        assertEquals(newTime, time);
        session.close();
    }

    @Test
    @DisplayName("getFreeTimeForDate with valid data - should return a list of RecordTime")
    public void getFreeTimeForDate() {
        // given
        Date date1 = Date.valueOf("2021-10-18"); // there's 1 free RecordTime for this date
        Date date2 = Date.valueOf("2021-01-01"); // there are no Records with this date, so every RecordTime for it is free

        // when
        List<RecordTime> timeList1 = repo.getFreeTimeForDate(date1);
        List<RecordTime> timeList2 = repo.getFreeTimeForDate(date2);

        // then
        assertEquals(1, timeList1.size());
        assertEquals(3, timeList2.size());
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain deleted Record;" +
            "method shouldn't delete user with this record")
    public void deleteRecord() {
        // given
        session = getSessionFactory().openSession();
        Record record = session.find(Record.class, 1L);
        session.close();

        // when
        repo.deleteRecord(record);

        // then
        session = getSessionFactory().openSession();
        assertNull(session.find(Record.class, 1L));
        User user = session.find(User.class, 1L);
        assertNotNull(user);
        assertEquals(1, user.getRecords().size());
        session.close();
    }
}
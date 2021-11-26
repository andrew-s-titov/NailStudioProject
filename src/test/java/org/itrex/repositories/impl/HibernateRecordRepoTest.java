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

import java.time.LocalDate;
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
        assertEquals(1, records.get(0).getClient().getUserId());
        assertEquals(4, records.get(0).getStaff().getUserId());
        assertEquals(LocalDate.of(2021, 10, 18), records.get(0).getDate());
        assertEquals(RecordTime.NINE, records.get(0).getTime());
        assertEquals(2, records.get(3).getClient().getUserId());
        assertEquals(1, records.get(3).getStaff().getUserId());
        assertEquals(RecordTime.SEVENTEEN, records.get(3).getTime());
    }

    @Test
    @DisplayName("getRecordById with valid data - should return existing Record")
    public void getRecordById1() {
        // given & when
        Record record1 = repo.getRecordById(1L);
        Record record3 = repo.getRecordById(3L);

        // then
        assertEquals(1, record1.getRecordId());
        assertEquals(1, record1.getClient().getUserId());
        assertEquals(4, record1.getStaff().getUserId());
        assertEquals(RecordTime.NINE, record1.getTime());
        assertEquals(LocalDate.of(2021, 10, 18), record1.getDate());
        assertEquals(3, record3.getRecordId());
        assertEquals(RecordTime.THIRTEEN, record3.getTime());
        assertEquals(LocalDate.of(2021, 10, 18), record3.getDate());
    }

    @Test
    @DisplayName("getRecordById with invalid data - should throw DatabaseEntryNotFoundException")
    public void getRecordById2() {
        // given
        Long recordId1 = 7L;
        Long recordId2 = 180L;

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> repo.getRecordById(recordId1));
        assertThrows(DatabaseEntryNotFoundException.class, () -> repo.getRecordById(recordId2));
    }

    @Test
    @DisplayName("getRecordsForClient with valid data - should return a list of Records for User client")
    public void getRecordsForClient1() {
        // given & when
        Long userId = 1L; // User with this id has 2 records
        List<Record> records = repo.getRecordsForClient(userId);

        // then
        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("getRecordsForClient with invalid data - should return empty list")
    public void getRecordsForClient2() {
        // given
        long userId = 7L; // there are no Users with this id
        List<Record> records = repo.getRecordsForClient(userId);

        // when & then
        assertEquals(0, records.size());
    }

    @Test
    @DisplayName("getRecordsForStaffToDo with valid data - should return a list of Records for User client")
    public void getRecordsForStaffToDo1() {
        // given & when
        Long userId = 1L; // staff with this user_id has 1 records to-do
        List<Record> records = repo.getRecordsForStaffToDo(userId);

        // then
        assertEquals(1, records.size());
    }

    @Test
    @DisplayName("getRecordsForStaffToDo with invalid data - should return empty list")
    public void getRecordsForStaffToDo2() {
        // given
        long userId = 7L; // there are no Users with this id
        List<Record> records = repo.getRecordsForStaffToDo(userId);

        // when & then
        assertEquals(0, records.size());
    }

    @Test
    @DisplayName("createRecord with valid data - records table should contain added Record")
    public void createRecord() {
        // given
        session = getSessionFactory().openSession();
        Long userId = 1L; // this User client has 2 records
        Long staffId = 4L;
        User client = session.find(User.class, userId);
        User staff = session.find(User.class, staffId);
        session.close();

        Record record = Record.builder()
                .date(LocalDate.of(2021, 12, 30))
                .time(RecordTime.NINE)
                .client(client)
                .staff(staff)
                .build();

        // when
        Record createdRecord = repo.createRecord(record);

        // then
        session = getSessionFactory().openSession();
        assertEquals(recordsTableInitialTestSize + 1, repo.getAll().size());
        assertEquals(1, session.createQuery("FROM Record WHERE date = :date")
                .setParameter("date", LocalDate.of(2021, 12, 30))
                .list().size());
        assertEquals(LocalDate.of(2021, 12, 30), createdRecord.getDate());
        session.close();
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain deleted Record;" +
            "method shouldn't delete user with this record")
    public void deleteRecord() {
        // given
        session = getSessionFactory().openSession();
        Record record = session.find(Record.class, 1L);
        User client = record.getClient(); // user_id 1
        User staff = record.getStaff(); // staff_id 4
        session.close();

        // when
        repo.deleteRecord(record);

        // then
        session = getSessionFactory().openSession();
        assertNull(session.find(Record.class, 1L));
        assertEquals(recordsTableInitialTestSize - 1,
                session.createQuery("FROM Record", Record.class).list().size());
        assertNotNull(session.find(User.class, client.getUserId()));
        assertNotNull(session.find(User.class, staff.getUserId()));
        session.close();
    }
}
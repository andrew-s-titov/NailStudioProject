package org.itrex.services.impl;

import org.hibernate.Session;
import org.itrex.TestBaseHibernate;
import org.itrex.dto.RecordCreateDTO;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.exceptions.DatabaseEntryNotFoundException;
import org.itrex.services.RecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecordServiceImplTest extends TestBaseHibernate {
    @Autowired
    private RecordService service;
    private final int recordsTableInitialTestSize = 4;
    private Session session;

    @Test
    @DisplayName("getAll - should return 4 Records equal to testdata migration script")
    public void getAll() {
        // given & when
        List<RecordCreateDTO> records = service.getAll();

        // then
        assertEquals(recordsTableInitialTestSize, records.size());
        assertEquals(1, records.get(0).getUserId());
        assertEquals("2021-10-18", records.get(0).getDate());
        assertEquals(RecordTime.NINE, records.get(0).getTime());
        assertEquals(2, records.get(3).getUserId());
        assertEquals(RecordTime.SEVENTEEN, records.get(3).getTime());
    }

    @Test
    @DisplayName("getRecordById with valid data - should return existing Record")
    public void getRecordById1() {
        // given & when
        RecordCreateDTO recordCreateDTO1 = service.getRecordById(1L);
        RecordCreateDTO recordCreateDTO2 = service.getRecordById(3L);

        // then
        assertEquals(1, recordCreateDTO1.getRecordId());
        assertEquals(RecordTime.NINE, recordCreateDTO1.getTime());
        assertEquals("2021-10-18", recordCreateDTO1.getDate());
        assertEquals(3, recordCreateDTO2.getRecordId());
        assertEquals(RecordTime.THIRTEEN, recordCreateDTO2.getTime());
        assertEquals("2021-10-18", recordCreateDTO2.getDate());
    }

    @Test
    @DisplayName("getRecordById with invalid data - should throw DatabaseEntryNotFoundException")
    public void getRecordById2() {
        // given
        long recordId1 = 7L;
        long recordId2 = 180L;

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getRecordById(recordId1));
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getRecordById(recordId2));
    }

    @Test
    @DisplayName("getRecordsForUserByUserId with valid data - should return a list of Records for User")
    public void getRecordsForUserByUserId1() {
        // given & when
        long userId = 1L; // User with this id has 2 records
        List<RecordCreateDTO> records = service.getRecordsForUser(userId);

        // then
        assertEquals(2, records.size());
        assertEquals(2, records.stream().filter(r -> r.getUserId() == userId).count());
    }

    @Test
    @DisplayName("getRecordsForUserByUserId with invalid data - should return empty list")
    public void getRecordsForUserByUserId2() {
        // given
        long userId = 7L; // there are no Users with this id
        List<RecordCreateDTO> records = service.getRecordsForUser(userId);

        // when & then
        assertEquals(0, records.size());
    }

    @Test
    @DisplayName("addRecordForUser with valid data - records table should contain record with given parameters")
    public void addRecordForUser() {
        // given
        long userId = 1L;
        long numberOfRecordsForUser = 2L;
        RecordCreateDTO record = RecordCreateDTO.builder()
                .date("2021-10-19")
                .time(RecordTime.NINE)
                .build();

        // when
        service.createRecordForClient(userId, record);

        // then
        session = getSessionFactory().openSession();
        assertEquals(recordsTableInitialTestSize + 1,
                session.createQuery("FROM Record", Record.class).list().size());
        assertEquals(numberOfRecordsForUser + 1, session.get(User.class, userId).getRecords().size());
        session.close();
    }

    @Test
    @DisplayName("addRecordForUser with invalid data - shouldn't add record and should return BookingUnavailableException message")
    public void addRecordForUser2() {
        // given
        long userId = 1L;
        RecordCreateDTO record = RecordCreateDTO.builder()
                .date("2021-10-18")
                .time(RecordTime.NINE) // this time has been already booked for given date
                .build();

        // when & then
        assertEquals("This time has already been booked!", service.createRecordForClient(userId, record));
        session = getSessionFactory().openSession();
        assertEquals(recordsTableInitialTestSize,
                session.createQuery("FROM Record", Record.class).list().size());
        session.close();
    }

    @Test
    @DisplayName("changeRecordTime with valid data - records table should update time for this record")
    public void changeRecordTime1() {
        // given
        long recordId = 1L; // time 09:00
        RecordTime newTime = RecordTime.SEVENTEEN; // not booked for the same date

        // when
        service.changeRecordTime(recordId, newTime);

        // then
        session = getSessionFactory().openSession();

        assertEquals(newTime, session.find(Record.class, recordId).getTime());
        assertEquals(recordsTableInitialTestSize, session.createQuery("FROM Record", Record.class).list().size());

        session.close();
    }

    @Test
    @DisplayName("changeRecordTime with invalid data - shouldn't change time, should return BookingUnavailableException message")
    public void changeRecordTime2() {
        // given
        long recordId = 1L; // time 09:00
        RecordTime currentTime = RecordTime.NINE;
        RecordTime newTime = RecordTime.THIRTEEN; // already booked for the same date

        // when & then
        assertEquals("This time has already been booked!", service.changeRecordTime(recordId, newTime));
        session = getSessionFactory().openSession();

        assertEquals(currentTime, session.find(Record.class, recordId).getTime());
        assertEquals(recordsTableInitialTestSize, session.createQuery("FROM Record", Record.class).list().size());

        session.close();
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain Record with given ID")
    public void deleteRecord() {
        // given
        long recordId = 1L;
        long userIdWithThisRecord = 1L;

        // when
        service.deleteRecord(recordId);

        // then
        session = getSessionFactory().openSession();

        assertNull(session.find(Record.class, recordId));
        User user = session.find(User.class, userIdWithThisRecord);
        assertNotNull(session.find(User.class, userIdWithThisRecord));
        assertEquals(recordsTableInitialTestSize - 1,
                session.createQuery("FROM Record", Record.class).list().size());

        session.close();
    }
}

package org.itrex.service.impl;

import org.hibernate.Session;
import org.itrex.TestBaseHibernate;
import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.Record;
import org.itrex.entity.User;
import org.itrex.entity.enums.RecordTime;
import org.itrex.exception.BookingUnavailableException;
import org.itrex.service.RecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RecordServiceImplTest extends TestBaseHibernate {
    @Autowired
    private RecordService service;
    private final int recordsTableInitialTestSize = 4;
    private Session session;

    @Test
    @DisplayName("getAll - should return all RecordForAdminDTO equal to testdata migration script")
    public void getAll() {
        // given & when
        List<RecordForAdminDTO> records = service.getAll();

        // then
        assertEquals(recordsTableInitialTestSize, records.size());

        assertEquals(1, records.get(0).getRecordId());
        assertEquals(RecordTime.NINE, records.get(0).getTime());
        assertEquals(LocalDate.of(2021, 10, 18), records.get(0).getDate());
        assertEquals("wow@gmail.com", records.get(0).getClientEmail());
        assertEquals("Senior", records.get(0).getStaffLastName());
    }

    @Test
    @DisplayName("getRecordsForClient with valid data - should return a list of RecordOfClientDTO for User client")
    public void getRecordsForClient1() {
        // given
        Long userId = 1L; // User with this id has 2 records as client

        // when
        List<RecordOfClientDTO> recordsForClient = service.getRecordsForClient(userId);

        // then
        assertEquals(2, recordsForClient.size());
        System.out.println(recordsForClient.get(0).getStaffLastName());
        assertEquals(2, recordsForClient.stream().filter(r -> r.getStaffLastName().equals("Senior")).count());
    }

    @Test
    @DisplayName("getRecordsForClient with invalid data - should return empty list")
    public void getRecordsForClient2() {
        // given
        Long userId = 7L; // there are no Users with this id

        // when
        List<RecordOfClientDTO> recordsForClient = service.getRecordsForClient(userId);

        // when
        assertEquals(0, recordsForClient.size());
    }

    @Test
    @DisplayName("getRecordsForStaffToDo with valid data - should return a list of RecordForStaffToDoDTO for User staff")
    public void getRecordsForStaffToDo1() {
        // given
        Long staffId = 1L; // User with this id has 1 record as staff to-do and 1 done record-task

        // when
        List<RecordForStaffToDoDTO> recordsForStaffToDo = service.getRecordsForStaffToDo(staffId);

        // then
        assertEquals(1, recordsForStaffToDo.size());
        assertEquals("bp@yahoo.com", recordsForStaffToDo.get(0).getClientEmail());
    }

    @Test
    @DisplayName("getRecordsForStaffToDo with invalid data - should return empty list")
    public void getRecordsForStaffToDo2() {
        // given
        Long userId = 7L; // there are no Users with this id

        // when
        List<RecordForStaffToDoDTO> recordsForStaffToDo = service.getRecordsForStaffToDo(userId);

        // then
        assertEquals(0, recordsForStaffToDo.size());
    }

    @Test
    @DisplayName("getFreeRecordsFor3MonthsByStaffId with valid data - should return hashmap " +
            "without info about booked records for User staff")
    public void getFreeRecordsFor3MonthsByStaffId() {
        // given
        Long staffId = 1L; // User with this id has 1 record as staff to-do

        // when
        Map<LocalDate, List<RecordTime>> result = service.getFreeRecordsFor3MonthsByStaffId(staffId);

        // then
        assertEquals(2, result.get(LocalDate.of(2021, 12, 31)).size());
        assertFalse(result.get(LocalDate.of(2021, 12, 31)).contains(RecordTime.SEVENTEEN));
    }

    @Test
    @DisplayName("createRecord with valid data - records table should contain record with given parameters")
    public void createRecord1() throws BookingUnavailableException {
        // given
        Long userId = 1L;
        Long staffId = 4L;
        RecordCreateDTO record = RecordCreateDTO.builder()
                .date(LocalDate.of(2021, 12, 30))
                .time(RecordTime.NINE)
                .clientId(userId)
                .staffId(staffId)
                .build();

        // when
        RecordOfClientDTO record1 = service.createRecord(record);

        // then
        session = getSessionFactory().openSession();
        List<Record> records = session.createQuery("FROM Record", Record.class).list();
        assertEquals(recordsTableInitialTestSize + 1, records.size());
        assertEquals(1, records.stream()
                .filter(r -> r.getDate().equals(record.getDate()))
                .count());
        assertEquals(5, record1.getRecordId());
        session.close();
    }

    @Test
    @DisplayName("createRecord with invalid data - shouldn't add record and should throw BookingUnavailableException message")
    public void createRecord2() {
        // given
        Long userId = 2L;
        Long staffId = 1L;
        RecordCreateDTO record = RecordCreateDTO.builder()
                .date(LocalDate.of(2021, 12, 31))
                .time(RecordTime.SEVENTEEN) // this time has been already booked for given date
                .clientId(userId)
                .staffId(staffId)
                .build();

        // when & then
        assertThrows(BookingUnavailableException.class, () -> service.createRecord(record));
    }

    @Test
    @DisplayName("deleteRecord with valid data - records table shouldn't contain Record with given ID")
    public void deleteRecord() {
        // given
        Long recordId = 1L;
        Long clientId = 1L;
        Long staffId = 4L;

        // when
        service.deleteRecord(recordId);

        // then
        session = getSessionFactory().openSession();

        assertNull(session.find(Record.class, recordId));
        assertNotNull(session.find(User.class, clientId));
        assertNotNull(session.find(User.class, staffId));
        assertEquals(recordsTableInitialTestSize - 1,
                session.createQuery("FROM Record", Record.class).list().size());

        session.close();
    }
}

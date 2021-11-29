package org.itrex.service.impl;

import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.Record;
import org.itrex.entity.User;
import org.itrex.entity.enums.RecordTime;
import org.itrex.exception.BookingUnavailableException;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.repository.RecordRepo;
import org.itrex.repository.UserRepo;
import org.itrex.service.RecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RecordServiceImplTest {
    @MockBean
    private RecordRepo recordRepo;
    @MockBean
    private UserRepo userRepo;
    @Autowired
    private RecordService service;

    private final User client = new User(1L, "password1".getBytes(StandardCharsets.UTF_8),
            "Andrew", "T", "375293000000", "wow@gmail.com");
    private final User staff = new User(4L, "password2".getBytes(StandardCharsets.UTF_8),
            "Staff", "Senior", "375295055055", "staff1@nailstudio.com");
    private final Record record1 = new Record(1L, LocalDate.of(2021, 10, 18),
            RecordTime.NINE, client, staff);
    private final Record record2 = new Record(2L, LocalDate.of(2021, 12, 31),
            RecordTime.SEVENTEEN, client, staff);

    @Test
    @DisplayName("getAll - should return list of RecordForAdminDTO with data from Records")
    public void getAll() {
        // given
        List<Record> records = Arrays.asList(record1, record2);
        when(recordRepo.getAll()).thenReturn(records);

        // when
        List<RecordForAdminDTO> returnedRecords = service.getAll();

        // then
        assertEquals(records.size(), returnedRecords.size());

        assertEquals(record1.getRecordId(), returnedRecords.get(0).getRecordId());
        assertEquals(record1.getDate(), returnedRecords.get(0).getDate());
        assertEquals(record1.getTime(), returnedRecords.get(0).getTime());
        assertEquals("wow@gmail.com", returnedRecords.get(0).getClientEmail());
        assertEquals("Senior", returnedRecords.get(0).getStaffLastName());

        assertEquals(record2.getRecordId(), returnedRecords.get(1).getRecordId());
        assertEquals(record2.getDate(), returnedRecords.get(1).getDate());
        assertEquals(record2.getTime(), returnedRecords.get(1).getTime());

        verify(recordRepo).getAll();
    }

    @Test
    @DisplayName("getRecordsForClient with valid data - should return a list of RecordOfClientDTO for User client")
    public void getRecordsForClient1() throws DatabaseEntryNotFoundException {
        // given
        Long userId = 1L;
        when(recordRepo.getRecordsForClient(userId)).thenReturn(Arrays.asList(record1, record2));
        when(userRepo.getUserById(userId)).thenReturn(Optional.of(client));

        // when
        List<RecordOfClientDTO> recordsForClient = service.getRecordsForClient(userId);

        // then
        assertEquals(2, recordsForClient.size());
        assertEquals(2, recordsForClient.stream()
                .filter(r -> r.getStaffLastName().equals(record1.getStaff().getLastName()))
                .count());

        verify(userRepo).getUserById(userId);
        verify(recordRepo).getRecordsForClient(userId);
    }

    @Test
    @DisplayName("getRecordsForClient with invalid data - should throw DatabaseEntryNotFoundException")
    public void getRecordsForClient2() {
        // given
        Long userId = 7L; // there are no Users with this id
        when(userRepo.getUserById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getRecordsForClient(userId));

        verify(userRepo).getUserById(userId);
        verify(recordRepo, never()).getRecordsForClient(userId);
    }

    @Test
    @DisplayName("getRecordsForStaffToDo with valid data - should return a list of RecordForStaffToDoDTO for User staff")
    public void getRecordsForStaffToDo1() throws DatabaseEntryNotFoundException {
        // given
        Long staffId = 4L; // User with this id has 1 record-task as staff to-do and 1 done record-task
        when(userRepo.getUserById(staffId)).thenReturn(Optional.of(staff));
        when(recordRepo.getRecordsForStaffToDo(staffId)).thenReturn(Collections.singletonList(record2));

        // when
        List<RecordForStaffToDoDTO> recordsForStaffToDo = service.getRecordsForStaffToDo(staffId);

        // then
        assertEquals(1, recordsForStaffToDo.size());
        assertEquals(client.getEmail(), recordsForStaffToDo.get(0).getClientEmail());
        assertEquals(record2.getDate(), recordsForStaffToDo.get(0).getDate());

        verify(userRepo).getUserById(staffId);
        verify(recordRepo).getRecordsForStaffToDo(staffId);
    }

    @Test
    @DisplayName("getRecordsForStaffToDo with invalid data - should throw DatabaseEntryNotFoundException")
    public void getRecordsForStaffToDo2() {
        // given
        Long staffId = 7L; // there are no Users with this id
        when(userRepo.getUserById(staffId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.getRecordsForStaffToDo(staffId));

        verify(userRepo).getUserById(staffId);
        verify(recordRepo, never()).getRecordsForStaffToDo(staffId);
    }

    @Test
    @DisplayName("getFreeRecordsFor3MonthsByStaffId with valid data - should return hashmap " +
            "without info about booked records for User staff")
    public void getFreeRecordsFor3MonthsByStaffId() throws DatabaseEntryNotFoundException {
        // given
        Long staffId = 4L; // User with this id has 1 record as staff to-do
        when(userRepo.getUserById(staffId)).thenReturn(Optional.of(staff));
        when(recordRepo.getRecordsForStaffToDo(staffId)).thenReturn(Collections.singletonList(record2));

        // when
        Map<LocalDate, List<RecordTime>> result = service.getFreeRecordsFor3MonthsByStaffId(staffId);

        // then
        assertEquals(2, result.get(LocalDate.of(2021, 12, 31)).size()); // one time-slot booked
        assertFalse(result.get(LocalDate.of(2021, 12, 31)).contains(RecordTime.SEVENTEEN));

        verify(userRepo).getUserById(staffId);
        verify(recordRepo).getRecordsForStaffToDo(staffId);
    }

    @Test
    @DisplayName("createRecord with valid data - shouldn't throw exceptions, returned RecordOfClientDTO should have " +
            "data equal to passed RecordCreateDTO")
    public void createRecord1() throws BookingUnavailableException, DatabaseEntryNotFoundException {
        // given
        RecordCreateDTO recordCreateDTO = new RecordCreateDTO(LocalDate.of(2021, 12, 30),
                RecordTime.NINE, client.getUserId(), staff.getUserId());
        Record newRecord = getNewRecord();
        Record createdRecord = getNewRecord();
        createdRecord.setRecordId(3L);

        when(userRepo.getUserById(client.getUserId())).thenReturn(Optional.of(client));
        when(userRepo.getUserById(staff.getUserId())).thenReturn(Optional.of(staff));
        when(recordRepo.getRecordsForStaffToDo(staff.getUserId())).thenReturn(Collections.singletonList(record2));
        when(recordRepo.createRecord(newRecord)).thenReturn(createdRecord);

        // when
        RecordOfClientDTO record = service.createRecord(recordCreateDTO);

        // then
        assertEquals(3L, record.getRecordId());
        assertEquals(record.getDate(), record.getDate());
        assertEquals(record.getTime(), record.getTime());

        verify(userRepo).getUserById(client.getUserId());
        verify(userRepo).getUserById(staff.getUserId());
        verify(recordRepo).getRecordsForStaffToDo(staff.getUserId());
        verify(recordRepo).createRecord(newRecord);
    }

    @Test
    @DisplayName("createRecord with invalid data - should throw BookingUnavailableException")
    public void createRecord2() {
        // given
        RecordCreateDTO newRecord = new RecordCreateDTO(LocalDate.of(2021, 12, 31),
                RecordTime.SEVENTEEN, client.getUserId(), staff.getUserId());
        when(userRepo.getUserById(client.getUserId())).thenReturn(Optional.of(client));
        when(userRepo.getUserById(staff.getUserId())).thenReturn(Optional.of(staff));
        when(recordRepo.getRecordsForStaffToDo(staff.getUserId())).thenReturn(Collections.singletonList(record2));

        // when & then
        assertThrows(BookingUnavailableException.class, () -> service.createRecord(newRecord));

        verify(userRepo).getUserById(client.getUserId());
        verify(userRepo).getUserById(staff.getUserId());
        verify(recordRepo).getRecordsForStaffToDo(staff.getUserId());
        verify(recordRepo, never()).createRecord(record2);
    }

    @Test
    @DisplayName("deleteRecord with valid data - shouldn't throw exceptions")
    public void deleteRecord1() {
        // given
        Long recordId = record1.getRecordId();
        when(recordRepo.getRecordById(recordId)).thenReturn(Optional.of(record1));
        doNothing().when(recordRepo).deleteRecord(record1);

        // when & then
        assertDoesNotThrow(() -> service.deleteRecord(recordId));

        verify(recordRepo).getRecordById(recordId);
        verify(recordRepo).deleteRecord(record1);
    }

    @Test
    @DisplayName("deleteRecord with invalid data - should throw DatabaseEntryNotFoundException")
    public void deleteRecord2() {
        // given
        Long recordId = 10500L;
        when(recordRepo.getRecordById(recordId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DatabaseEntryNotFoundException.class, () -> service.deleteRecord(recordId));

        verify(recordRepo).getRecordById(recordId);
        verify(recordRepo, never()).deleteRecord(record1);
    }

    private Record getNewRecord() {
        Record record = Record.builder()
                .date(LocalDate.of(2021, 12, 30))
                .time(RecordTime.NINE)
                .build();
        record.setClient(client);
        record.setStaff(staff);
        return record;
    }
}

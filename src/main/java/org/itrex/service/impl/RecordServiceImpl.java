package org.itrex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.converter.RecordDTOConverter;
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
import org.mockito.Mockito;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecordServiceImpl implements RecordService {
    private final RecordRepo recordRepo;
    private final UserRepo userRepo;
    private final RecordDTOConverter converter;

    @Override
    public List<RecordForAdminDTO> getAll() {
        return recordRepo.getAll().stream()
                .map(converter::toRecordForAdminDTO)
                .collect(Collectors.toList());
        // TODO: filter to show only active records or paging
    }

    @Override
    public List<RecordOfClientDTO> getRecordsForClient(Long clientId) throws DatabaseEntryNotFoundException {
        // TODO: change tests, add postman queries for exception
        Optional<User> optionalUser = userRepo.getUserById(clientId);
        if (optionalUser.isEmpty()) {
            String message = String.format("User (client) with id %s wasn't found.", clientId);
            log.debug("DatabaseEntryNotFoundException was thrown while executing getRecordsForClient method: {}", message);
            throw new DatabaseEntryNotFoundException(message);
        }
        return recordRepo.getRecordsForClient(clientId).stream()
                .map(converter::toRecordOfClientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordForStaffToDoDTO> getRecordsForStaffToDo(Long staffId) throws DatabaseEntryNotFoundException {
        // TODO: change tests, add postman queries for exception
        Optional<User> optionalUser = userRepo.getUserById(staffId);
        if (optionalUser.isEmpty()) {
            String message = String.format("User (staff) with id %s wasn't found.", staffId);
            log.debug("DatabaseEntryNotFoundException was thrown while executing getRecordsForStaffToDo method: {}", message);
            throw new DatabaseEntryNotFoundException(message);
        }
        return recordRepo.getRecordsForStaffToDo(staffId).stream()
                .map(converter::toRecordForStaffToDoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RecordOfClientDTO createRecord(RecordCreateDTO recordCreateDTO)
            throws BookingUnavailableException, DatabaseEntryNotFoundException {
        String message;

        Optional<User> optionalClient = userRepo.getUserById(recordCreateDTO.getClientId());
        if (optionalClient.isEmpty()) {
            message = String.format("User (client) with id %s wasn't found.", recordCreateDTO.getClientId());
            log.debug("DatabaseEntryNotFoundException was thrown while executing createRecord method: {}", message);
            throw new DatabaseEntryNotFoundException(message);
        }

        Optional<User> optionalStaff = userRepo.getUserById(recordCreateDTO.getStaffId());
        if (optionalStaff.isEmpty()) {
            message = String.format("User (staff) with id %s wasn't found.", recordCreateDTO.getClientId());
            log.debug("DatabaseEntryNotFoundException was thrown while executing createRecord method: {}", message);
            throw new DatabaseEntryNotFoundException(message);
        }

        Record newRecord = converter.fromRecordCreateDTO(recordCreateDTO);
        newRecord.setClient(optionalClient.get());
        newRecord.setStaff(optionalStaff.get());

        checkRecordAvailability(recordCreateDTO);

        Record createdRecord = recordRepo.createRecord(newRecord);
        return converter.toRecordOfClientDTO(createdRecord);
    }

    @Override
    public void deleteRecord(Long recordId) throws DatabaseEntryNotFoundException {
        Optional<Record> optionalRecord = recordRepo.getRecordById(recordId);
        if (optionalRecord.isEmpty()) {
            String message = String.format("Record with id %s wasn't found.", recordId);
            log.debug("DatabaseEntryNotFoundException was thrown while executing getRecordById method: {}", message);
            throw new DatabaseEntryNotFoundException(message);
        }
        recordRepo.deleteRecord(optionalRecord.get());
    }

    @Override
    public Map<LocalDate, List<RecordTime>> getFreeRecordsFor3MonthsByStaffId(Long staffId) throws DatabaseEntryNotFoundException {
        Optional<User> optionalUser = userRepo.getUserById(staffId);
        if (optionalUser.isEmpty()) {
            String message = String.format("User (staff) with id %s wasn't found.", staffId);
            log.debug("DatabaseEntryNotFoundException was thrown while executing getRecordsForStaffToDo method: {}", message);
            throw new DatabaseEntryNotFoundException(message);
        }

        LocalDate now = LocalDate.now();
        LocalDate lastDate = now.plusMonths(3);

        Map<LocalDate, List<RecordTime>> timeSlotsFor3Months = new TreeMap<>();

        // fill the map with every time slot for every day in 3 months range
        LocalDate startDate = now;
        while (!startDate.equals(lastDate)) {
            timeSlotsFor3Months.put(startDate, new ArrayList<>(Arrays.asList(RecordTime.values())));
            startDate = startDate.plusDays(1);
        }

        // remove booked time slot from the map for staff person
        List<Record> recordsForStaffToDo = recordRepo.getRecordsForStaffToDo(staffId);
        recordsForStaffToDo.stream()
                .filter(r -> timeSlotsFor3Months.containsKey(r.getDate()))
                .forEach(r -> timeSlotsFor3Months.get(r.getDate()).remove(r.getTime()));

        return timeSlotsFor3Months;
    }

    private void checkRecordAvailability(RecordCreateDTO record) throws BookingUnavailableException {
        boolean booked = recordRepo.getRecordsForStaffToDo(record.getStaffId()).stream()
                .anyMatch(r -> r.getDate().equals(record.getDate()) && r.getTime().equals(record.getTime()));
        if (booked) {
            LocalDate date = record.getDate();
            RecordTime time = record.getTime();
            throw new BookingUnavailableException(date, time);
        }
    }
}
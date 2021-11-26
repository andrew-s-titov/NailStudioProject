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
import org.itrex.repository.RecordRepo;
import org.itrex.repository.RoleRepo;
import org.itrex.repository.UserRepo;
import org.itrex.service.RecordService;
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
    private final RoleRepo roleRepo;
    private final RecordDTOConverter converter;

    @Override
    public List<RecordForAdminDTO> getAll() {
        return recordRepo.getAll().stream()
                .map(converter::toRecordForAdminDTO)
                .collect(Collectors.toList());
        // TODO: filter to show only active records or paging
    }

    @Override
    public List<RecordOfClientDTO> getRecordsForClient(Long clientId) {
        return recordRepo.getRecordsForClient(clientId).stream()
                .map(converter::toRecordOfClientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordForStaffToDoDTO> getRecordsForStaffToDo(Long staffId) {
        return recordRepo.getRecordsForStaffToDo(staffId).stream()
                .map(converter::toRecordForStaffToDoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long createRecord(RecordCreateDTO recordCreateDTO) throws BookingUnavailableException {
        User client = userRepo.getUserById(recordCreateDTO.getUserId());
        User staff = userRepo.getUserById(recordCreateDTO.getStaffId());
        Record newRecord = converter.fromRecordCreateDTO(recordCreateDTO);
        newRecord.setClient(client);
        newRecord.setStaff(staff);
        checkRecordAvailability(recordCreateDTO);
        return recordRepo.createRecord(newRecord).getRecordId();
    }

    @Override
    public void deleteRecord(Long recordId) {
        Record recordEntity = recordRepo.getRecordById(recordId);
        recordRepo.deleteRecord(recordEntity);
    }

    @Override
    public HashMap<LocalDate, List<RecordTime>> getFreeRecordsFor3MonthsByStaffId(Long staffId) {
        LocalDate now = LocalDate.now();
        LocalDate lastDate = now.plusMonths(3);

        HashMap<LocalDate, List<RecordTime>> timeSlotsFor3Months = new HashMap<>();

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
            throw new BookingUnavailableException();
        }
    }
}
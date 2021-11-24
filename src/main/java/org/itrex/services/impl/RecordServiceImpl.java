package org.itrex.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.converters.RecordDTOConverter;
import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.exceptions.BookingUnavailableException;
import org.itrex.repositories.RecordRepo;
import org.itrex.repositories.UserRepo;
import org.itrex.services.RecordService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
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
    }

    @Override
    public List<RecordOfClientDTO> getRecordsForUser(Long clientId) {
        return recordRepo.getRecordsForUser(clientId).stream()
                .map(converter::toRecordOfClientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordForStaffToDoDTO> getRecordsForStaffToDo(Long staffId) {
        return recordRepo.getRecordsForUser(staffId).stream()
                .map(converter::toRecordForStaffToDoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long createRecordForClient(RecordCreateDTO recordCreateDTO) throws BookingUnavailableException {
        User client = userRepo.getUserById(recordCreateDTO.getUserId());
        User staff = userRepo.getUserById(recordCreateDTO.getStaffId());
        RecordTime time = recordCreateDTO.getTime();
        Date date = Date.valueOf(recordCreateDTO.getDate());
        // TODO: add logic - check depending on staff free slots
        checkTimeAvailability(date, time);
        Record newRecord = converter.fromRecordCreateDTO(recordCreateDTO);
        // TODO: check implicit setClient (createRecordForClient calls User.addRecord)
        newRecord.setClient(client);
        newRecord.setStaff(staff);
        // TODO: check createRecord logic
        recordRepo.createRecordForClient(client, newRecord);
        // TODO: change return
        return 0L;
    }

    @Override
    public void deleteRecord(Long recordId) {
        Record recordEntity = recordRepo.getRecordById(recordId);
        recordRepo.deleteRecord(recordEntity);
    }

    private void checkTimeAvailability(Date date, RecordTime newTime) throws BookingUnavailableException {
        if (!recordRepo.getFreeTimeForDate(date).contains(newTime)) {
            throw new BookingUnavailableException();
        }
    }
}
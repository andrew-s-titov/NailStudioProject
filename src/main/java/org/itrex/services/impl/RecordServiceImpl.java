package org.itrex.services.impl;

import lombok.RequiredArgsConstructor;
import org.itrex.dto.RecordDTO;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.exceptions.BookingUnavailableException;
import org.itrex.repositories.RecordRepo;
import org.itrex.repositories.UserRepo;
import org.itrex.services.RecordService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecordServiceImpl implements RecordService {
    private final RecordRepo recordRepo;
    private final UserRepo userRepo;

    @Override
    public List<RecordDTO> getAll() {
        return recordRepo.getAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordDTO> getRecordsForUserByUserId(Serializable id) {
        return recordRepo.getRecordsForUserByUserId(id).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RecordDTO getRecordById(Serializable id) {
        return entityToDTO(recordRepo.getRecordById(id));
    }

    @Override
    public String addRecordForUser(Serializable userId, RecordDTO recordDTO) {
        User user = userRepo.getUserById(userId);
        RecordTime time = recordDTO.getTime();
        Date date = Date.valueOf(recordDTO.getDate());
        try {
            checkTimeAvailability(date, time);
            Record newRecord = fromDTO(recordDTO);
            recordRepo.addRecordForUser(user, newRecord);
            return "Booking successful";
        } catch (BookingUnavailableException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public String changeRecordTime(Serializable recordId, RecordTime newTime) {
        Record recordEntity = recordRepo.getRecordById(recordId);
        Date date = recordEntity.getDate();
        try {
            checkTimeAvailability(date, newTime);
            recordRepo.changeRecordTime(recordEntity, newTime);
            return "Booking successful";
        } catch (BookingUnavailableException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public void deleteRecord(Serializable recordId) {
        Record recordEntity = recordRepo.getRecordById(recordId);
        recordRepo.deleteRecord(recordEntity);
    }

    private void checkTimeAvailability(Date date, RecordTime newTime) throws BookingUnavailableException {
        if (!recordRepo.getFreeTimeForDate(date).contains(newTime)) {
            throw new BookingUnavailableException();
        }
    }

    private RecordDTO entityToDTO(Record recordEntity) {
        return RecordDTO.builder()
                .recordId(recordEntity.getRecordId())
                .date(recordEntity.getDate().toString())
                .time(recordEntity.getTime())
                .userId(recordEntity.getUser().getUserId())
                .build();
    }

    private Record fromDTO(RecordDTO recordDTO) {
        return Record.builder()
                .date(Date.valueOf(recordDTO.getDate()))
                .time(recordDTO.getTime())
                .build();
    }
}
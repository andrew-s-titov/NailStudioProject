package org.itrex.services;

import org.hibernate.Session;
import org.itrex.dto.RecordDTO;
import org.itrex.entities.Record;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.impl.HibernateRecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordService extends ServiceWithTransactionWrapping {
    private final HibernateRecordRepo recordRepo;

    public RecordService(@Autowired HibernateRecordRepo recordRepo) {
        this.recordRepo = recordRepo;
    }

    public List<RecordDTO> getAll() {
        List<RecordDTO> records = recordRepo.getAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
        recordRepo.closeRepoSession();
        return records;
    }

    public RecordDTO getRecordById(Serializable id) {
        Record recordEntity = recordRepo.getRecordById(id);
        recordRepo.closeRepoSession();
        return entityToDTO(recordEntity);
    }

    public void changeRecordTime(long recordId, RecordTime newTime) {
        Record recordEntity = recordRepo.getRecordById(recordId);
        Date date = recordEntity.getDate();
        if (TimeAvailability.getFreeTimeForDate(recordRepo.getCurrentSession(), date).contains(newTime)) {
            throw new IllegalStateException("This time has been already booked!");
        } else {
            recordRepo.changeRecordTime(recordEntity, newTime);
        }
        recordRepo.closeRepoSession();
    }

    public void deleteRecord(long recordId) {
        Record recordEntity = recordRepo.getRecordById(recordId);
        doInTransaction(recordRepo.getCurrentSession(), () -> recordRepo.deleteRecord(recordEntity));
        recordRepo.closeRepoSession();
    }

    private RecordDTO entityToDTO(Record recordEntity) {
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setRecordId(recordEntity.getRecordId());
        recordDTO.setDate(recordEntity.getDate().toString());
        recordDTO.setTime(recordEntity.getTime().digitsText);
        return recordDTO;
    }
}
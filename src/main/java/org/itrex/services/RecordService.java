package org.itrex.services;

import org.itrex.dto.RecordDTO;
import org.itrex.entities.enums.RecordTime;

import java.io.Serializable;
import java.util.List;

public interface RecordService {
    List<RecordDTO> getAll();

    List<RecordDTO> getRecordsForUserByUserId(Serializable id);

    RecordDTO getRecordById(Serializable id);

    String changeRecordTime(Serializable recordId, RecordTime newTime);

    void deleteRecord(Serializable recordId);
}

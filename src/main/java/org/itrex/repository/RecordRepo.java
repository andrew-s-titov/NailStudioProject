package org.itrex.repository;

import org.itrex.entity.Record;

import java.util.List;
import java.util.Optional;

public interface RecordRepo {
    List<Record> getAll();

    Optional<Record> getRecordById(Long id);

    List<Record> getRecordsForClient(Long clientId);

    List<Record> getRecordsForStaffToDo(Long staffId);

    Record createRecord(Record record);

    void deleteRecord(Record record);
}
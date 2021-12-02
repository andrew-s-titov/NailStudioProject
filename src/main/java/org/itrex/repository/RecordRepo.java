package org.itrex.repository;

import org.itrex.entity.Record;

import java.util.List;
import java.util.Optional;

@Deprecated
public interface RecordRepo {
    List<Record> findAll();

    Optional<Record> findById(Long id);

    List<Record> getRecordsForClient(Long clientId);

    List<Record> getRecordsForStaffToDo(Long staffId);

    Record save(Record record);

    void deleteRecord(Record record);
}
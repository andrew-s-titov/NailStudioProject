package org.itrex.repository;

import org.itrex.entity.Record;

import java.util.List;

public interface RecordRepo {
    List<Record> getAll();

    Record getRecordById(Long id);

    List<Record> getRecordsForClient(Long clientId);

    List<Record> getRecordsForStaffToDo(Long staffId);

    Record createRecord(Record record);

    void deleteRecord(Record record);
}
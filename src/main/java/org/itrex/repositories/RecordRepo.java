package org.itrex.repositories;

import org.itrex.entities.Record;
import org.itrex.entities.enums.RecordTime;

import java.io.Serializable;
import java.util.List;

public interface RecordRepo {
    List<Record> getAll();

    Record getRecordById(Serializable id);

    void changeRecordTime(Record record, RecordTime newTime);

    void deleteRecord(Record record);
}
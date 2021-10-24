package org.itrex.repositories;

import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;

import java.util.List;

public interface RecordRepo {
    List<Record> selectAll();

    void addRecord(Record record);

    void changeRecordTime(Record record, RecordTime newTime);

    void deleteRecord(Record record);

    void deleteRecordsForUser(User user);
}
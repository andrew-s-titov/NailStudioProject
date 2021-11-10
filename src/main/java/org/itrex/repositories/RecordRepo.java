package org.itrex.repositories;

import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public interface RecordRepo {
    List<Record> getAll();

    Record getRecordById(Serializable id);

    List<Record> getRecordsForUserByUserId(Serializable userId);

    void addRecordForUser(User user, Record record);

    void changeRecordTime(Record record, RecordTime newTime);

    List<RecordTime> getFreeTimeForDate(Date date);

    void deleteRecord(Record record);
}
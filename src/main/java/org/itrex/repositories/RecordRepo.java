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

    List<Record> getRecordsForUser(Serializable userId);

    void createRecordForClient(User client, Record record);

    List<RecordTime> getFreeTimeForDate(Date date);

    void deleteRecord(Record record);
}
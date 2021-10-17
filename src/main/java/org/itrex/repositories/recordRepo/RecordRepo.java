package org.itrex.repositories.recordRepo;

import org.itrex.entities.Record;
import org.itrex.entities.User;

import java.util.List;

public interface RecordRepo {
    List<Record> selectAll();

    void addRecord(Record record);

    void deleteRecord(Record record);
}
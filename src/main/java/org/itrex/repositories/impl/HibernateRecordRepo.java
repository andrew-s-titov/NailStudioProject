package org.itrex.repositories.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Record;
import org.itrex.exceptions.DatabaseEntryNotFoundException;
import org.itrex.repositories.RecordRepo;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class HibernateRecordRepo implements RecordRepo {
    private final SessionFactory sessionFactory;
    private Session session;

    @Override
    public List<Record> getAll() {
        session = sessionFactory.openSession();
        List<Record> records = session.createQuery("FROM Record", Record.class).list();
        session.close();
        return records;
    }

    @Override
    public Record getRecordById(Long id) {
        session = sessionFactory.openSession();
        Record record = session.get(Record.class, id);
        session.close();
        if (record == null) {
            String message = String.format("Record with id %s wasn't found.", id);
            log.debug(message + "DatabaseEntryNotFoundException was thrown while executing getRecordById method.");
            throw new DatabaseEntryNotFoundException(message);
        }
        return record;
    }

    @Override
    public List<Record> getRecordsForClient(Long clientId) {
        session = sessionFactory.openSession();
        List<Record> records = session.createQuery("FROM Record WHERE client_id = :id", Record.class)
                .setParameter("id", clientId)
                .list();
        session.close();
        return records;
    }

    @Override
    public List<Record> getRecordsForStaffToDo(Long staffId) {
        session = sessionFactory.openSession();
        List<Record> records = session.createQuery("FROM Record WHERE staff_id = :id AND date >= :date", Record.class)
                .setParameter("id", staffId)
                .setParameter("date", LocalDate.now())
                .list();
        session.close();
        return records;
    }

    @Override
    public Record createRecord(Record record) {
        Long recordId;
        session = sessionFactory.openSession();
        recordId = (Long) session.save(record);
        Record createdRecord = session.load(Record.class, recordId);
        session.close();
        return createdRecord;
    }

    @Override
    public void deleteRecord(Record record) {
        inSession(() -> {
            session.beginTransaction();
            session.update(record);
            session.delete(record);
            session.getTransaction().commit();
        });
    }

    private void inSession(Runnable runnable) {
        try {
            session = sessionFactory.openSession();
            runnable.run();
        } finally {
            session.close(); // without finally there's a connection leak if method exits with an exception
        }
    }
}
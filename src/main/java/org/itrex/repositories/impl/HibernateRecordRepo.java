package org.itrex.repositories.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.exceptions.DatabaseEntryNotFoundException;
import org.itrex.repositories.RecordRepo;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
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
    public Record getRecordById(Serializable id) {
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
    public List<Record> getRecordsForUserByUserId(Serializable userId) {
        session = sessionFactory.openSession();
        List<Record> records = session.createQuery("FROM Record WHERE user_id = :id", Record.class)
                .setParameter("id", userId)
                .list();
        session.close();
        return records;
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        inSession(() -> {
            session.beginTransaction();
            session.update(record);
            record.setTime(newTime);
            session.getTransaction().commit();
        });
    }

    @Override
    public void deleteRecord(Record record) {
        inSession(() -> {
            session.beginTransaction();
            session.delete(record);
            session.getTransaction().commit();
        });
    }

    @Override
    public void addRecordForUser(User user, Record record) {
        inSession(() -> {
            session.beginTransaction();
            session.update(user);
            user.addRecord(record);
            session.getTransaction().commit();
        });
    }

    public List<RecordTime> getFreeTimeForDate(Date date) {
        session = sessionFactory.openSession();
        final List<RecordTime> availableTime = new ArrayList<>(Arrays.asList(RecordTime.values()));
        List<Record> recordsWithGivenDate = session.createQuery("FROM Record WHERE date = :date", Record.class)
                .setParameter("date", date)
                .list();
        session.close();
        if (!recordsWithGivenDate.isEmpty()) {
            recordsWithGivenDate.stream()
                    .map(Record::getTime)
                    .forEach(availableTime::remove);
        }
        return availableTime;
    }

    private void inSession(Runnable runnable) {
        session = sessionFactory.openSession();
        runnable.run();
        session.close();
    }
}
package org.itrex.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
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
import java.util.List;
import java.util.stream.Collectors;

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
            String message = String.format("Record with id %s wasn't found", id);
            throw new DatabaseEntryNotFoundException(message);
        }
        return record;
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        inSession(() -> record.setTime(newTime));
    }

    @Override
    public void deleteRecord(Record record) {
        inSession(() -> {
            session.beginTransaction();
            try {
                record.getUser().removeRecord(record);
                session.getTransaction().commit();
            } catch (HibernateException ex) {
                ex.printStackTrace();
                session.getTransaction().rollback();
            }
        });
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
    public void addRecordForUser(User user, Record record) {
        inSession(() -> {
            user.addRecord(record);
            session.save(record);
        });
    }


    public List<RecordTime> getFreeTimeForDate(Date date) {
        session = sessionFactory.openSession();
        List<RecordTime> availableTime = session.createQuery("FROM Record WHERE date = :date", Record.class)
                .setParameter("date", date)
                .list()
                .stream()
                .map(Record::getTime)
                .collect(Collectors.toList());
        session.close();
        return availableTime;
    }

    private void inSession(Runnable runnable) {
        session = sessionFactory.openSession();
        runnable.run();
        session.close();
    }
}
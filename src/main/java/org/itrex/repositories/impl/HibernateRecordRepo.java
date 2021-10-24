package org.itrex.repositories.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.RecordRepo;

import java.util.List;


public class HibernateRecordRepo implements RecordRepo {
    private final Session session;

    public HibernateRecordRepo(Session session) {
        this.session = session;
    }

    @Override
    public List<Record> selectAll() {
        return session.createQuery("FROM Record", Record.class).list();
    }

    @Override
    public void addRecord(Record record) {
        Transaction txn = session.beginTransaction();
        try {
            session.save(record);
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        Transaction txn = session.beginTransaction();
        try {
            record.setTime(newTime);
            session.update(record);
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }

    @Override
    public void deleteRecord(Record record) {
        Transaction txn = session.beginTransaction();
        try {
            session.delete(record);
            record.getUser().getRecords().remove(record);
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }

    @Override
    public void deleteRecordsForUser(User user) {
        Transaction txn = session.beginTransaction();
        try {
            long id = user.getUserId();
            session.createQuery("DELETE FROM Record WHERE user_id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            user.getRecords().clear();
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }
}
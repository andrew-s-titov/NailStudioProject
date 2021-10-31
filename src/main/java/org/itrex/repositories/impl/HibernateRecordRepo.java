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
        doInTransaction(() -> record.getUser().addRecord(record));
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        doInTransaction(() -> record.setTime(newTime));
    }

    @Override
    public void deleteRecord(Record record) {
        doInTransaction(() -> record.getUser().removeRecord(record));
    }

    @Override
    public void deleteRecordsForUser(User user) {
        doInTransaction(() -> user.getRecords().clear());
    }

    private void doInTransaction(Runnable runnable) {
        Transaction txn = session.beginTransaction();
        try {
            runnable.run();
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }
}
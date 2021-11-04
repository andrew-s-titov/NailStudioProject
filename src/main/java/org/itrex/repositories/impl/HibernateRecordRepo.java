package org.itrex.repositories.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.RecordRepo;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class HibernateRecordRepo implements RecordRepo {
    private Session session;

    public HibernateRecordRepo() {
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getCurrentSession() {
        return session;
    }

    @Override
    public List<Record> selectAll() {
        if (session == null || !session.isOpen()) {
            return Collections.emptyList();
        }
        return session.createQuery("FROM Record", Record.class).list();
    }

    @Override
    public void addRecord(Record record) {
        if (session == null || !session.isOpen()) {
            return;
        }
        doInTransaction(() -> record.getUser().addRecord(record));
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        if (session == null || !session.isOpen()) {
            return;
        }
        doInTransaction(() -> record.setTime(newTime));
    }

    @Override
    public void deleteRecord(Record record) {
        if (session == null || !session.isOpen()) {
            return;
        }
        doInTransaction(() -> record.getUser().removeRecord(record));
    }

    @Override
    public void deleteRecordsForUser(User user) {
        if (session == null || !session.isOpen()) {
            return;
        }
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

    private void checkSession() {
        if (session == null || !session.isOpen()) {
            return;
        }
    }
}
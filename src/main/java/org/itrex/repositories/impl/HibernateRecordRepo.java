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
    // maybe we should create methods addRecord and deleteRecord is User entity?
    public void addRecord(Record record) {
        doInTransaction(() -> {
            session.save(record);
        });
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        doInTransaction(() -> {
            record.setTime(newTime);
//            session.update(record);
        });
    }

    @Override
    public void deleteRecord(Record record) {
        doInTransaction(() -> {
            session.delete(record);
            record.getUser().getRecords().remove(record);
        });
    }

    @Override
    public void deleteRecordsForUser(User user) {
        doInTransaction(() -> {
        long id = user.getUserId();
        session.createQuery("DELETE FROM Record WHERE user_id = :id")
                .setParameter("id", id)
                .executeUpdate();
        user.getRecords().clear();
        });
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
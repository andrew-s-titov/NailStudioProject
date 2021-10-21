package org.itrex.repositories.recordRepo.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.repositories.recordRepo.RecordRepo;

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
        session.save(record);
    }

    @Override
    public void deleteRecord(Record record) {
        Transaction txn = session.beginTransaction();
        session.delete(record);
        txn.commit();

        record.getUser().getRecords().remove(record);
    }

    @Override
    public void deleteRecordsForUser(User user) {
        Transaction txn = session.beginTransaction();
        long id = user.getUserId();
        session.createQuery("DELETE FROM Record WHERE user_id = :id")
                .setParameter("id", id)
                .executeUpdate();
        txn.commit();

        user.getRecords().clear();
    }
}

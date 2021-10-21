package org.itrex.repositories.recordRepo.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.repositories.recordRepo.RecordRepo;

import java.util.List;


public class HibernateRecordRepo implements RecordRepo {
    private static final String TABLE_NAME = "records";
    private static final String RECORD_ID_COLUMN = "record_id";
    private static final String USER_ID_COLUMN = "user_id";

    private static final String DELETE_RECORD_QUERY = String.format("DELETE FROM %s WHERE %s = ?",
            TABLE_NAME, RECORD_ID_COLUMN);
    private static final String DELETE_USER_RECORD_QUERY = String.format("DELETE FROM %s WHERE %s = ?",
            TABLE_NAME, USER_ID_COLUMN);

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
        session.delete(record);
    }

    @Override
    public void deleteRecordsForUser(User user) {
        Transaction txn = session.beginTransaction();
        long id = user.getUserId();
        session.createQuery("DELETE from Record WHERE userId = :id")
                .setParameter("id", id)
                .executeUpdate();
        txn.commit();
    }
}

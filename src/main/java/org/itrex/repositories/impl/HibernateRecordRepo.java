package org.itrex.repositories.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.RecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HibernateRecordRepo extends BaseHibernateRepoWithSessionManagement implements RecordRepo {
    private final SessionFactory sessionFactory;
    private Session session;

    public HibernateRecordRepo(@Autowired SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void openSession() {
        if (!isSessionActive(session)) {
            this.session = sessionFactory.openSession();
        }
    }

    public void closeRepoSession() {
        if (isSessionActive(session)) {
            session.close();
        }
    }

    public Session getCurrentSession() {
        openSession();
        return session;
    }

    @Override
    public List<Record> selectAll() {
        openSession();
        return session.createQuery("FROM Record", Record.class).list();
    }

    @Override
    public void addRecord(Record record) {
        openSession();
        doInTransaction(session, () -> record.getUser().addRecord(record));
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        openSession();
        doInTransaction(session, () -> record.setTime(newTime));
    }

    @Override
    public void deleteRecord(Record record) {
        openSession();
        doInTransaction(session, () -> record.getUser().removeRecord(record));
    }

    @Override
    public void deleteRecordsForUser(User user) {
        openSession();
        doInTransaction(session, () -> user.getRecords().clear());
    }
}
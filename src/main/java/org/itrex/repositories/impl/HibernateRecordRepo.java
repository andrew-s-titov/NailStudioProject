package org.itrex.repositories.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Record;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.HibernateBaseRepoWithSessionManagement;
import org.itrex.repositories.RecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public class HibernateRecordRepo implements RecordRepo, HibernateBaseRepoWithSessionManagement {
    private final SessionFactory sessionFactory;
    private Session session;

    public HibernateRecordRepo(@Autowired SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void openSession() {
        if (!isSessionActive(session)) {
            this.session = sessionFactory.openSession();
        }
    }

    @Override
    public void closeRepoSession() {
        if (isSessionActive(session)) {
            session.close();
        }
    }

    @Override
    public Session getCurrentSession() {
        openSession();
        return session;
    }

    @Override
    public List<Record> getAll() {
        openSession();
        return session.createQuery("FROM Record", Record.class).list();
    }

    @Override
    public Record getRecordById(Serializable id) {
        openSession();
        return session.get(Record.class, id);
    }

    @Override
    public void changeRecordTime(Record record, RecordTime newTime) {
        openSession();
        record.setTime(newTime);
    }

    @Override
    public void deleteRecord(Record record) {
        openSession();
        record.getUser().removeRecord(record);
    }
}
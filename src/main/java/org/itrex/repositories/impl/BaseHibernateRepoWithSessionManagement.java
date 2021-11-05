package org.itrex.repositories.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class BaseHibernateRepoWithSessionManagement {

    abstract void openSession();

    abstract void closeRepoSession();

    abstract Session getCurrentSession();

    protected boolean isSessionActive(Session session) {
        return (session != null && session.isOpen());
    }

    protected void doInTransaction(Session session, Runnable runnable) {
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
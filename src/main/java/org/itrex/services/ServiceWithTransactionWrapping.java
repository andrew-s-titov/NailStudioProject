package org.itrex.services;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class ServiceWithTransactionWrapping {
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
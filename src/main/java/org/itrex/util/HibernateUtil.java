package org.itrex.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    // javadoc: implementation *must* be threadsafe
    // TODO: thread-safe Singleton

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            }
            catch (HibernateException e) {
                // TODO: logging
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void closeFactory() {
        sessionFactory.close();
    }
}

package org.itrex.repositories;

import org.hibernate.Session;

public interface HibernateBaseRepoWithSessionManagement {
    void openSession();

    void closeRepoSession();

    Session getCurrentSession();

    default boolean isSessionActive(Session session) {
        return (session != null && session.isOpen());
    }
}
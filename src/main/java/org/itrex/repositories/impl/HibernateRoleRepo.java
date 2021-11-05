package org.itrex.repositories.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.itrex.entities.Role;
import org.itrex.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HibernateRoleRepo extends BaseHibernateRepoWithSessionManagement implements RoleRepo {
    private final SessionFactory sessionFactory;
    private Session session;

    public HibernateRoleRepo (@Autowired SessionFactory sessionFactory) {
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
    public List<Role> getRoles() {
        openSession();
        return session.createQuery("FROM Role", Role.class).list();
    }

    @Override
    public Role getRoleByName(String name) {
        openSession();
        Query<Role> query = session.createQuery("FROM Role WHERE role_name = :name", Role.class);
        query.setParameter("name", name.toUpperCase());
        return query.getSingleResult();
    }
}
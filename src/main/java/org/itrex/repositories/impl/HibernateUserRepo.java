package org.itrex.repositories.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HibernateUserRepo extends BaseHibernateRepoWithSessionManagement implements UserRepo {
    private final SessionFactory sessionFactory;
    private Session session;

    public HibernateUserRepo(@Autowired SessionFactory sessionFactory) {
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
    public List<User> selectAll() {
        openSession();
        return session.createQuery("FROM User", User.class).list();
    }

    @Override
    public void addUser(User user) {
        openSession();
        doInTransaction(session, () -> session.save(user));
    }

    @Override
    public void deleteUser(User user) {
        openSession();
        doInTransaction(session, () -> session.delete(user));
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        openSession();
        doInTransaction(session, () -> user.setEmail(newEmail));
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        openSession();
        doInTransaction(session, () -> user.setDiscount(discount));
    }

    @Override
    public void addRoleForUser(User user, Role role) {
        openSession();
        doInTransaction(session, () -> user.getUserRoles().add(role));
    }
}
package org.itrex.repositories.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.UserRepo;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class HibernateUserRepo implements UserRepo{
    private Session session;

    public HibernateUserRepo() {
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getCurrentSession() {
        return session;
    }

    @Override
    public List<User> selectAll() {
        if (session == null || !session.isOpen()) {
            return Collections.emptyList();
        }
        return session.createQuery("FROM User", User.class).list();
    }

    @Override
    public void addUser(User user) {
        if (session == null || !session.isOpen()) {
            return;
        }
        doInTransaction(() -> session.save(user));
    }

    @Override
    public void deleteUser(User user) {
        if (session == null || !session.isOpen()) {
            return;
        }
        doInTransaction(() -> session.delete(user));
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        if (session == null || !session.isOpen()) {
            return;
        }
        doInTransaction(() -> user.setEmail(newEmail));
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        if (session == null || !session.isOpen()) {
            return;
        }
        doInTransaction(() -> user.setDiscount(discount));
    }

    @Override
    public void addRoleForUser(User user, Role role) {
        if (session == null || !session.isOpen()) {
            return;
        }
        doInTransaction(() -> user.getUserRoles().add(role));
    }

    private void doInTransaction(Runnable runnable) {
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
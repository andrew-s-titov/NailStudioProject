package org.itrex.repositories.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.UserRepo;

import java.util.List;

public class HibernateUserRepo implements UserRepo {

    private final Session session;

    public HibernateUserRepo(Session session) {
        this.session = session;
    }

    @Override
    public List<User> selectAll() {
        return session.createQuery("FROM User", User.class).list();
    }

    @Override
    public void addUser(User user) {
        doInTransaction(() -> session.save(user));
    }

    @Override
    public void deleteUser(User user) {
        doInTransaction(() -> session.delete(user));
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        doInTransaction(() -> user.setEmail(newEmail));
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        doInTransaction(() -> user.setDiscount(discount));
    }

    public void addRoleForUser(User user, Role role) {
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
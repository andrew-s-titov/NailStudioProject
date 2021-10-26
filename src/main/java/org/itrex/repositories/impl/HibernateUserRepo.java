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
        doInTransaction(() -> {
            session.delete(user);
        });
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        doInTransaction(() -> {
            user.setEmail(newEmail);
//            session.update(user);
        });
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        doInTransaction(() -> {
            user.setDiscount(discount);
//            session.update(user);
        });
    }

    public void addRoleForUser(User user, Role role) {
        // should be applied to a persistent User entity
        // can be used with persistent Role entity or transient Role entity with the appropriate id
        doInTransaction(() -> {
            user.getUserRoles().add(role);
//            role.getUsers().add(user);
//            session.update(user);
//            session.update(role);
        });
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
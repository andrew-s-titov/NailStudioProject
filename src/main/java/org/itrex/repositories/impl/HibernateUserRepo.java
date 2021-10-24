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
        Transaction txn = session.beginTransaction();
        try {
            session.save(user);
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }

    @Override
    public void deleteUser(User user) {
        Transaction txn = session.beginTransaction();
        try {
            session.delete(user);
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        Transaction txn = session.beginTransaction();
        try {
            user.setEmail(newEmail);
            session.update(user);
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        Transaction txn = session.beginTransaction();
        try {
            user.setDiscount(discount);
            session.update(user);
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }

    public void addRole(User user, Role role) {
        Transaction txn = session.beginTransaction();
        try {
            user.getUserRoles().add(role);
            role.getUsers().add(user); // not necessary
            session.update(user);
            session.update(role);
            txn.commit();
        } catch (HibernateException e) {
            txn.rollback();
            e.printStackTrace();
            // TODO: logging
        }
    }
}
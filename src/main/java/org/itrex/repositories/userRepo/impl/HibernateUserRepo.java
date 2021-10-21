package org.itrex.repositories.userRepo.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.userRepo.UserRepo;

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
        session.save(user);
    }

    @Override
    public void addUserList(List<User> users) {
        Transaction txn = session.beginTransaction();

        try {
            for (User user : users) {
                session.save(user);
            }
            txn.commit();
        } catch (Exception e) {
            if (txn != null) txn.rollback();
            throw e;
            // TODO: logging
        }
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        user.setEmail(newEmail);
        session.update(user);
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        user.setDiscount(discount);
        session.update(user);
    }
}
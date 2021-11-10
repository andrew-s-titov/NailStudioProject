package org.itrex.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.exceptions.DatabaseEntryNotFoundException;
import org.itrex.repositories.UserRepo;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class HibernateUserRepo implements UserRepo {
    private final SessionFactory sessionFactory;
    private Session session;

    @Override
    public User findUserById(Serializable id) {
        User user;
        session = sessionFactory.openSession();
        user = session.get(User.class, id);
        session.close();
        if (user == null) {
            String message = String.format("User with id %s wasn't found", id);
            throw new DatabaseEntryNotFoundException(message);
        }
        return user;
    }

    @Override
    public User findUserByPhone(String phone) {
        Optional<User> user;
        session = sessionFactory.openSession();
        user = session.createQuery("FROM User WHERE phone = :phone", User.class)
                .setParameter("phone", phone)
                .list()
                .stream()
                .findAny();
        session.close();
        return user.orElse(null);
    }

    @Override
    public List<User> getAll() {
        session = sessionFactory.openSession();
        List<User> users = session.createQuery("FROM User", User.class).list();
        session.close();
        return users;
    }

    @Override
    public void addUser(User user) {
        inSession(() -> session.save(user));
    }

    @Override
    public void deleteUser(User user) {
        inSession(() -> {
            session.beginTransaction();
            try {
                session.delete(user);
                session.getTransaction().commit();
            } catch (HibernateException ex) {
                ex.printStackTrace();
                session.getTransaction().rollback();
            }
        });
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        inSession(() -> user.setEmail(newEmail));
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        inSession(() -> user.setDiscount(discount));
    }

    @Override
    public void addRoleForUser(User user, Role role) {
        inSession(() -> user.getUserRoles().add(role));
    }

    private void inSession(Runnable runnable) {
        session = sessionFactory.openSession();
        runnable.run();
        session.close();
    }
}
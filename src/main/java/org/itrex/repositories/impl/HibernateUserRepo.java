package org.itrex.repositories.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.exceptions.DatabaseEntryNotFoundException;
import org.itrex.repositories.UserRepo;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class HibernateUserRepo implements UserRepo {
    private final SessionFactory sessionFactory;
    private Session session;

    @Override
    public User getUserById(Serializable id) {
        User user;
        session = sessionFactory.openSession();
        user = session.get(User.class, id);
        session.close();
        if (user == null) {
            String message = String.format("User with id %s wasn't found", id);
            log.debug(message + "DatabaseEntryNotFoundException was thrown while executing getUserById method.");
            throw new DatabaseEntryNotFoundException(message);
        }
        return user;
    }

    @Override
    public User getUserByPhone(String phone) {
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
                log.debug(Arrays.toString(ex.getStackTrace()));
                session.getTransaction().rollback();
            }
        });
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        inSession(() -> {
            session.update(user);
            session.beginTransaction();
            user.setEmail(newEmail);
            session.getTransaction().commit();
        });
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        inSession(() -> {
            session.update(user);
            session.beginTransaction();
            user.setDiscount(discount);
            session.getTransaction().commit();
        });
    }

    @Override
    public void addRoleForUser(User user, Role role) {
        inSession(() -> {
            session.beginTransaction();
            session.update(user);
            user.getUserRoles().add(role);
            session.getTransaction().commit();
        });
    }

    private void inSession(Runnable runnable) {
        try {
            session = sessionFactory.openSession();
            runnable.run();
        } finally {
            session.close(); // without finally there's a connection leak if method exits with an exception
        }
    }
}
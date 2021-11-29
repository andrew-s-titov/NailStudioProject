package org.itrex.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entity.User;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.repository.UserRepo;
import org.springframework.stereotype.Repository;

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
    public List<User> getAll() {
        List<User> users;
        session = sessionFactory.openSession();
        users = session.createQuery("FROM User", User.class).list();
        session.close();
        return users;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        Optional <User> optionalUser = Optional.empty();
        session = sessionFactory.openSession();
        User user = session.get(User.class, userId);
        session.close();
        if (user != null) {
            optionalUser = Optional.of(user);
        }
        return optionalUser;
    }

    @Override
    public Optional<User> getUserByPhone(String phone) {
        Optional <User> optionalUser;
        session = sessionFactory.openSession();
        optionalUser = session.createQuery("FROM User WHERE phone = :phone", User.class)
                .setParameter("phone", phone)
                .list()
                .stream()
                .findAny();
        session.close();
        return optionalUser;
    }

    @Override
    public User createUser(User user) {
        Long userId;
        session = sessionFactory.openSession();
        userId = (Long) session.save(user);
        User createdUser = session.load(User.class, userId);
        session.close();
        return createdUser;
    }

    @Override
    public void deleteUser(User user) {
        inSession(() -> {
            session.beginTransaction();
            try {
                session.delete(session.merge(user));
                session.getTransaction().commit();
            } catch (HibernateException ex) {
                log.debug(Arrays.toString(ex.getStackTrace()));
                session.getTransaction().rollback();
            }
        });
    }

    @Override
    public void updateUserInfo(User user) {
        inSession(() -> {
            session.beginTransaction();
            session.update(user);
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
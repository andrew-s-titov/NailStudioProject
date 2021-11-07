package org.itrex.repositories.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.HibernateBaseRepoWithSessionManagement;
import org.itrex.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public class HibernateUserRepo implements UserRepo, HibernateBaseRepoWithSessionManagement {
    private final SessionFactory sessionFactory;
    private Session session;

    public HibernateUserRepo(@Autowired SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void openSession() {
        if (!isSessionActive(session)) {
            this.session = sessionFactory.openSession();
        }
    }

    @Override
    public void closeRepoSession() {
        if (isSessionActive(session)) {
            session.close();
        }
    }

    @Override
    public Session getCurrentSession() {
        openSession();
        return session;
    }

    @Override
    public User findUserById(Serializable id) {
        openSession();
        return session.get(User.class, id);
    }

    @Override
    public User findUserByPhone(String phone) {
        openSession();
        Optional<User> user = session.createQuery("FROM User WHERE phone = :phone", User.class)
                .setParameter("phone", phone)
                .list()
                .stream()
                .findAny();
        return user.orElse(null);
    }

    @Override
    public List<User> getAll() {
        openSession();
        return session.createQuery("FROM User", User.class).list();
    }

    @Override
    public void addUser(User user) {
        openSession();
        session.save(user);
    }

    @Override
    public void deleteUser(User user) {
        openSession();
        session.delete(user);
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        openSession();
        user.setEmail(newEmail);
    }

    @Override
    public void changeDiscount(User user, Discount discount) {
        openSession();
        user.setDiscount(discount);
    }

    @Override
    public void addRecordForUser(User user, Record record) {
        openSession();
        user.addRecord(record);
    }

    @Override
    public void deleteRecordsForUser(User user) {
        openSession();
        user.getRecords().clear();
    }

    @Override
    public void addRoleForUser(User user, Role role) {
        openSession();
        user.getUserRoles().add(role);
    }
}
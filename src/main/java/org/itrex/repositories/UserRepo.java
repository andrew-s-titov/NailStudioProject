package org.itrex.repositories;

import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;

import java.io.Serializable;
import java.util.List;

public interface UserRepo {
    List<User> getAll();

    User findUserById(Serializable id);

    User findUserByPhone(String phone);

    void addUser(User user);

    void deleteUser(User user);

    void changeEmail(User user, String newEmail);

    void changeDiscount(User user, Discount discount);

    void addRecordForUser(User user, Record record);
    void deleteRecordsForUser(User user);

    void addRoleForUser(User user, Role role);
}
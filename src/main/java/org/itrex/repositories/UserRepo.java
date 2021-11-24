package org.itrex.repositories;

import org.itrex.entities.Role;
import org.itrex.entities.User;

import java.io.Serializable;
import java.util.List;

public interface UserRepo {
    List<User> getAll();

    User getUserById(Serializable id);

    User getUserByPhone(String phone);

    Long createUser(User user);

    void deleteUser(User user);

    void updateUserInfo(User user);

    void addRoleForUser(User user, Role role);
}
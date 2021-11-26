package org.itrex.repository;

import org.itrex.entity.Role;
import org.itrex.entity.User;

import java.util.List;

public interface UserRepo {
    List<User> getAll();

    User getUserById(Long userId);

    User getUserByPhone(String phone);

    User createUser(User user);

    void deleteUser(User user);

    void updateUserInfo(User user);

    void addRoleForUser(User user, Role role);
}
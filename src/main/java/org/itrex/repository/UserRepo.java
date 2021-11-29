package org.itrex.repository;

import org.itrex.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo {
    List<User> getAll();

    Optional<User> getUserById(Long userId);

    Optional<User> getUserByPhone(String phone);

    User createUser(User user);

    void deleteUser(User user);

    void updateUserInfo(User user);
}
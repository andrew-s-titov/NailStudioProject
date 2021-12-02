package org.itrex.repository;

import org.itrex.entity.User;

import java.util.List;
import java.util.Optional;

@Deprecated
public interface UserRepo {
    List<User> findAll();

    Optional<User> findById(Long userId);

    Optional<User> findByPhone(String phone);

    User save(User user);

    void delete(User user);

    void updateUserInfo(User user);
}
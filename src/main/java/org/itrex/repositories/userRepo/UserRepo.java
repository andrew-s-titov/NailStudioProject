package org.itrex.repositories.userRepo;

import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;

import java.util.List;

public interface UserRepo {
    List<User> selectAll();

    void addUser(User user);

    void addUserList(List<User> users);

    void deleteUser(User user);

    void changeEmail(User user, String newEmail);

    void changeDiscount(User user, Discount discount);
}
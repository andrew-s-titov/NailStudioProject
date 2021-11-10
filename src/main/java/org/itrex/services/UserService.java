package org.itrex.services;

import org.itrex.dto.UserDTO;
import org.itrex.entities.Role;
import org.itrex.entities.enums.Discount;

import java.io.Serializable;
import java.util.List;

public interface UserService {
    UserDTO findUserById(Serializable id);

    UserDTO findUserByPhone(String phone);

    List<UserDTO> getAll();

    String addUser(UserDTO user);

    String deleteUser(Serializable id);

    void changeEmail(Serializable userId, String newEmail);

    void changeDiscount(Serializable userId, Discount discount);

    void addRoleForUser(Serializable userId, Role role);
}

package org.itrex.services;

import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entities.enums.Discount;
import org.itrex.exceptions.DeletingUserWithActiveRecordsException;
import org.itrex.exceptions.UserExistsException;

import java.util.List;

public interface UserService {
    UserResponseDTO getUserById(Long userId);

    UserResponseDTO getUserByPhone(String phone);

    List<UserResponseDTO> getAll();

    Long createUser(UserCreateDTO userCreateDTO) throws UserExistsException;

    void deleteUser(Long userId) throws DeletingUserWithActiveRecordsException;

    // TODO: update user
    void updateUserInfo(UserUpdateDTO userUpdateDTO);

    void changeDiscount(Long clientId, Discount discount);

    // TODO: decide whether to leave or not
    void addRoleForUser(Long userId, String roleName);
}

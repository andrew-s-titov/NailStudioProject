package org.itrex.services;

import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entities.enums.Discount;
import org.itrex.exceptions.DeletingClientWithActiveRecordsException;
import org.itrex.exceptions.UserExistsException;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAll();

    UserResponseDTO getUserById(Long userId);

    Long createUser(UserCreateDTO userCreateDTO) throws UserExistsException;

    void deleteUser(Long userId) throws DeletingClientWithActiveRecordsException;

    void updateUserInfo(UserUpdateDTO userUpdateDTO);

    void changeClientDiscount(Long clientId, Discount newDiscount);

    void addRoleForUser(Long userId, String roleName);
}

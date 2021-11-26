package org.itrex.service;

import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.UserExistsException;

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

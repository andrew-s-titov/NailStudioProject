package org.itrex.service;

import org.itrex.dto.*;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAll();

    UserCreditsDTO getUserByPhone(String phone);

    UserResponseDTO getUserById(Long userId);

    UserResponseDTO createUser(UserCreateDTO userCreateDTO) throws UserExistsException;

    void deleteUser(Long userId) throws DeletingClientWithActiveRecordsException;

    void updateUserInfo(UserUpdateDTO userUpdateDTO);

    void changeClientDiscount(Long clientId, Discount newDiscount);

    void addRoleForUser(Long userId, RoleDTO roleDTO) throws RoleManagementException;

    void revokeRole(Long userId, RoleDTO roleDTO) throws RoleManagementException;
}

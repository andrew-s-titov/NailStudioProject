package org.itrex.service;

import org.itrex.dto.*;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAll(Pageable pageable);

    UserResponseDTO getUserById(Long userId) throws DatabaseEntryNotFoundException;

    UserCreditsDTO getUserByPhone(String phone) throws DatabaseEntryNotFoundException;

    UserResponseDTO createUser(UserCreateDTO userCreateDTO) throws UserExistsException;

    void deleteUser(Long userId) throws DeletingClientWithActiveRecordsException, DatabaseEntryNotFoundException;

    void updateUserInfo(UserUpdateDTO userUpdateDTO) throws DatabaseEntryNotFoundException;

    void changeClientDiscount(Long clientId, Discount newDiscount) throws DatabaseEntryNotFoundException;

    void addRoleForUser(Long userId, RoleDTO roleDTO) throws RoleManagementException, DatabaseEntryNotFoundException;

    void revokeRole(Long userId, RoleDTO roleDTO) throws RoleManagementException, DatabaseEntryNotFoundException;
}
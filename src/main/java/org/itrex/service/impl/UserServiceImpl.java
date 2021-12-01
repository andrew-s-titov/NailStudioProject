package org.itrex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.converter.RoleDTOConverter;
import org.itrex.converter.UserDTOConverter;
import org.itrex.dto.*;
import org.itrex.entity.Record;
import org.itrex.entity.Role;
import org.itrex.entity.User;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;
import org.itrex.repository.RecordRepo;
import org.itrex.repository.UserRepo;
import org.itrex.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RecordRepo recordRepo;
    private final UserDTOConverter userDTOConverter;
    private final RoleDTOConverter roleDTOConverter;

    @Override
    public UserResponseDTO getUserById(Long userId) throws DatabaseEntryNotFoundException {
        User user = checkIfUserExists(userId);
        return userDTOConverter.toUserResponseDTO(user);
    }

    @Override
    public UserCreditsDTO getUserByPhone(String phone) throws DatabaseEntryNotFoundException {
        Optional<User> optionalUser = userRepo.getUserByPhone(phone);
        if (optionalUser.isEmpty()) {
            String message = String.format("User with phone number %s wasn't found", phone);
            log.debug("DatabaseEntryNotFoundException was thrown while executing getUserById method: {}", message);
            throw new DatabaseEntryNotFoundException(message);
        }
        return userDTOConverter.toUserCreditsDTO(optionalUser.get());
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepo.getAll().stream()
                .map(userDTOConverter::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO user) throws UserExistsException {
        String phone = user.getPhone();
        if (userRepo.getUserByPhone(phone).isPresent()) {
            throw new UserExistsException(phone);
        }
        User newUser = userDTOConverter.fromUserCreateDTO(user);
        User createdUser = userRepo.createUser(newUser);
        return userDTOConverter.toUserResponseDTO(createdUser);
    }

    @Override
    public void deleteUser(Long userId) throws DeletingClientWithActiveRecordsException, DatabaseEntryNotFoundException {
        User user = checkIfUserExists(userId);
        List<Record> records = recordRepo.getRecordsForClient(userId);
        if (hasActiveRecords(records)) {
            throw new DeletingClientWithActiveRecordsException(userId);
        }
        userRepo.deleteUser(user);
    }

    @Override
    public void updateUserInfo(UserUpdateDTO userUpdateDTO) throws DatabaseEntryNotFoundException {
        User user = checkIfUserExists(userUpdateDTO.getUserId());
        boolean needUpdate = false;
        if (!user.getFirstName().equals(userUpdateDTO.getFirstName()) && userUpdateDTO.getFirstName() != null) {
            needUpdate = true;
            user.setFirstName(userUpdateDTO.getFirstName());
        }
        if (!user.getLastName().equals(userUpdateDTO.getLastName()) && userUpdateDTO.getLastName() != null) {
            needUpdate = true;
            user.setLastName(userUpdateDTO.getLastName());
        }
        if (!user.getEmail().equals(userUpdateDTO.getEmail()) && userUpdateDTO.getEmail() != null) {
            needUpdate = true;
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (needUpdate) { // avoiding unnecessary request to db
            userRepo.updateUserInfo(user);
        }
    }

    @Override
    public void changeClientDiscount(Long clientId, Discount newDiscount) throws DatabaseEntryNotFoundException {
        User user = checkIfUserExists(clientId);
        if (!user.getDiscount().equals(newDiscount)) { // avoiding unnecessary request to db
            user.setDiscount(newDiscount);
            userRepo.updateUserInfo(user);
        }
    }

    @Override
    public void addRoleForUser(Long userId, RoleDTO roleDTO)
            throws RoleManagementException, DatabaseEntryNotFoundException {

        User user = checkIfUserExists(userId);
        Role role = roleDTOConverter.fromRoleDTO(roleDTO);
        Set<Role> userRoles = user.getUserRoles();
        if (userRoles.contains(role)) {
            String message = String.format("Failed to add role: User with id %s already has role %s.",
                    userId, roleDTO.getRoleType().name());
            throw new RoleManagementException(message);
        }
        user.getUserRoles().add(role);
        userRepo.updateUserInfo(user);
    }

    @Override
    public void revokeRole(Long userId, RoleDTO roleDTO) throws RoleManagementException, DatabaseEntryNotFoundException {
        User user = checkIfUserExists(userId);
        Role role = roleDTOConverter.fromRoleDTO(roleDTO);
        Set<Role> userRoles = user.getUserRoles();
        if (userRoles.size() == 1) {
            String message = String.format("Failed to revoke role: User with id %s has only one role", userId);
            throw new RoleManagementException(message);
        }
        if (!userRoles.contains(role)) {
            String message = String.format("Failed to revoke role: User with id %s doesn't have role %s.",
                    userId, roleDTO.getRoleType().name());
            throw new RoleManagementException(message);
        }
        user.getUserRoles().remove(role);
        userRepo.updateUserInfo(user);
    }

    private boolean hasActiveRecords(List<Record> records) {
        if (!records.isEmpty()) {
            return records.stream()
                    .anyMatch(r -> r.getDate().compareTo(LocalDate.now()) >= 0);
        }
        return false;
    }

    private User checkIfUserExists(Long userId) throws DatabaseEntryNotFoundException {
        Optional<User> optionalUser = userRepo.getUserById(userId);
        if (optionalUser.isEmpty()) {
            String message = String.format("User with id %s wasn't found", userId);
            log.debug(message + "DatabaseEntryNotFoundException was thrown while executing getUserById method.");
            throw new DatabaseEntryNotFoundException(message);
        }
        return optionalUser.get();
    }
}
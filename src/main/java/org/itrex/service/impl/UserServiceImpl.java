package org.itrex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.converter.UserDTOConverter;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserCreditsDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entity.Record;
import org.itrex.entity.Role;
import org.itrex.entity.User;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;
import org.itrex.repository.RecordRepo;
import org.itrex.repository.RoleRepo;
import org.itrex.repository.UserRepo;
import org.itrex.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RecordRepo recordRepo;
    private final RoleRepo roleRepo;
    private final UserDTOConverter converter;

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User userEntity = userRepo.getUserById(userId);
        return converter.toUserResponseDTO(userEntity);
    }

    @Override
    public UserCreditsDTO getUserByPhone(String phone) {
        User userEntity = userRepo.getUserByPhone(phone);
        return userEntity == null ? null : converter.toUserCreditsDTO(userEntity);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepo.getAll().stream()
                .map(converter::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO user) throws UserExistsException {
        String phone = user.getPhone();
        checkExistingUser(phone);
        User newUser = converter.fromUserCreateDTO(user);
        User createdUser = userRepo.createUser(newUser);
        return converter.toUserResponseDTO(createdUser);
    }

    @Override
    public void deleteUser(Long userId) throws DeletingClientWithActiveRecordsException {
        User userEntity = userRepo.getUserById(userId);
        List<Record> records = recordRepo.getRecordsForClient(userId);
        checkActiveRecords(records);
        userRepo.deleteUser(userEntity);
    }

    @Override
    public void updateUserInfo(UserUpdateDTO userUpdateDTO) {
        User user = userRepo.getUserById(userUpdateDTO.getUserId());
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setEmail(userUpdateDTO.getEmail());
        userRepo.updateUserInfo(user);
    }

    @Override
    public void changeClientDiscount(Long clientId, Discount newDiscount) {
        User user = userRepo.getUserById(clientId);
        if (!user.getDiscount().equals(newDiscount)) { // avoiding unnecessary request to db
            user.setDiscount(newDiscount);
            userRepo.updateUserInfo(user);
        }
    }

    @Override
    public void addRoleForUser(Long userId, String roleName) throws RoleManagementException {
        User user = userRepo.getUserById(userId);
        Role role = roleRepo.getRoleByName(roleName);
        Set<Role> userRoles = user.getUserRoles();
        if (userRoles.contains(role)) {
            String message = String.format("Failed to add role: User with id %s already has role %s.",
                    userId, roleName.toUpperCase());
            throw new RoleManagementException(message);
        }
        user.getUserRoles().add(role);
        userRepo.updateUserInfo(user);
    }

    @Override
    public void revokeRole(Long userId, String roleName) throws RoleManagementException {
        User user = userRepo.getUserById(userId);
        Role role = roleRepo.getRoleByName(roleName);
        Set<Role> userRoles = user.getUserRoles();
        if (userRoles.size() == 1) {
            String message = String.format("Failed to revoke role: User with id %s has only one role", userId);
            throw new RoleManagementException(message);
        }
        if (!userRoles.contains(role)) {
            String message = String.format("Failed to revoke role: User with id %s doesn't have role %s.",
                    userId, roleName.toUpperCase());
            throw new RoleManagementException(message);
        }
        user.getUserRoles().remove(role);
        userRepo.updateUserInfo(user);
    }

    private void checkExistingUser(String phone) throws UserExistsException {
        if (userRepo.getUserByPhone(phone) != null) {
            throw new UserExistsException();
        }
    }

    private void checkActiveRecords(List<Record> records) throws DeletingClientWithActiveRecordsException {
        if (!records.isEmpty()) {
            boolean hasActiveRecords = records.stream()
                    .anyMatch(r -> r.getDate().compareTo(LocalDate.now()) >= 0);
            if (hasActiveRecords) {
                throw new DeletingClientWithActiveRecordsException();
            }
        }
    }
}
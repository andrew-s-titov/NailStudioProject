package org.itrex.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.exceptions.DeletingUserWithActiveRecordsException;
import org.itrex.exceptions.UserExistsException;
import org.itrex.repositories.RecordRepo;
import org.itrex.repositories.RoleRepo;
import org.itrex.repositories.UserRepo;
import org.itrex.services.UserService;
import org.itrex.util.PasswordEncryption;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RecordRepo recordRepo;
    private final RoleRepo roleRepo;

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User userEntity = userRepo.getUserById(userId);
        return toUserResponseDTO(userEntity);
    }

    @Override
    public UserResponseDTO getUserByPhone(String phone) {
        User userEntity = userRepo.getUserByPhone(phone);
        return userEntity == null ? null : toUserResponseDTO(userEntity);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepo.getAll().stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long createUser(UserCreateDTO user) throws UserExistsException {
        String phone = user.getPhone();
        checkExistingUser(phone);
        User newUser = fromUserCreateDTO(user);
        return userRepo.createUser(newUser);
    }

    @Override
    public void deleteUser(Long userId) throws DeletingUserWithActiveRecordsException {
        User userEntity = userRepo.getUserById(userId);
        List<Record> records = recordRepo.getRecordsForUser(userId);
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
    public void changeDiscount(Long clientId, Discount discount) {
        User user = userRepo.getUserById(clientId);
        user.setDiscount(discount);
        userRepo.updateUserInfo(user);
    }

    @Override
    public void addRoleForUser(Long userId, String roleName) {
        User user = userRepo.getUserById(userId);
        Role role = roleRepo.getRoleByName(roleName);
        userRepo.addRoleForUser(user, role);
    }

    private void checkExistingUser(String phone) throws UserExistsException {
        if (userRepo.getUserByPhone(phone) != null) {
            throw new UserExistsException();
        }
    }

    private void checkActiveRecords(List<Record> records) throws DeletingUserWithActiveRecordsException {
        if (!records.isEmpty()) {
            Date today = Date.valueOf(LocalDate.now());
            long futureRecordsCount = records.stream()
                    .filter(r -> r.getDate().compareTo(today) >= 0)
                    .count();
            if (futureRecordsCount > 0) {
                throw new DeletingUserWithActiveRecordsException();
            }
        }
    }

    private UserResponseDTO toUserResponseDTO(User userEntity) {
        return UserResponseDTO.builder()
                .userId(userEntity.getUserId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .discount(userEntity.getDiscount())
                .build();
    }

    private User fromUserCreateDTO(UserCreateDTO dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .password(PasswordEncryption.getEncryptedPassword(dto.getPassword()))
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
    }
}
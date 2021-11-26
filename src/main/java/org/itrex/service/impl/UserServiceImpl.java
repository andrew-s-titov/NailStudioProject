package org.itrex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entity.Record;
import org.itrex.entity.Role;
import org.itrex.entity.User;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.UserExistsException;
import org.itrex.repository.RecordRepo;
import org.itrex.repository.RoleRepo;
import org.itrex.repository.UserRepo;
import org.itrex.service.UserService;
import org.itrex.util.PasswordEncryption;
import org.springframework.stereotype.Service;

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
        return userRepo.createUser(newUser).getUserId();
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
        user.setDiscount(newDiscount);
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

    private void checkActiveRecords(List<Record> records) throws DeletingClientWithActiveRecordsException {
        if (!records.isEmpty()) {
            boolean hasActiveRecords = records.stream()
                    .anyMatch(r -> r.getDate().compareTo(LocalDate.now()) >= 0);
            if (hasActiveRecords) {
                throw new DeletingClientWithActiveRecordsException();
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
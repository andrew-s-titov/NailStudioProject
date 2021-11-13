package org.itrex.services.impl;

import lombok.RequiredArgsConstructor;
import org.itrex.dto.UserDTO;
import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RoleType;
import org.itrex.exceptions.DeletingUserWithActiveRecordsException;
import org.itrex.exceptions.UserExistsException;
import org.itrex.repositories.RecordRepo;
import org.itrex.repositories.RoleRepo;
import org.itrex.repositories.UserRepo;
import org.itrex.services.UserService;
import org.itrex.util.PasswordEncryption;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RecordRepo recordRepo;
    private final RoleRepo roleRepo;

    @Override
    public UserDTO findUserById(Serializable id) {
        User userEntity = userRepo.findUserById(id);
        return entityToDTO(userEntity);
    }

    @Override
    public UserDTO findUserByPhone(String phone) {
        User userEntity = userRepo.findUserByPhone(phone);
        return userEntity == null ? null : entityToDTO(userEntity);
    }

    @Override
    public List<UserDTO> getAll() {
        return userRepo.getAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String addUser(UserDTO user) {
        String phone = user.getPhone();
        try {
            checkExistingUser(phone);
            User userEntity = fromDTO(user);
            userRepo.addUser(userEntity);
            return "Registration successful";
        } catch (UserExistsException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public String deleteUser(Serializable id) {
        User userEntity = userRepo.findUserById(id);
        List<Record> records = recordRepo.getRecordsForUserByUserId(id);
        try {
            checkActiveRecords(records);
            userRepo.deleteUser(userEntity);
            return "User has been removed";
        } catch (DeletingUserWithActiveRecordsException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public void changeEmail(Serializable userId, String newEmail) {
        User user = userRepo.findUserById(userId);
        userRepo.changeEmail(user, newEmail);
    }

    @Override
    public void changeDiscount(Serializable userId, Discount discount) {
        User user = userRepo.findUserById(userId);
        userRepo.changeDiscount(user, discount);
    }

    @Override
    public void addRoleForUser(Serializable userId, String roleName) {
        User user = userRepo.findUserById(userId);
        Role role = roleRepo.getRoleByName(roleName);
        userRepo.addRoleForUser(user, role);
    }

    private void checkExistingUser(String phone) throws UserExistsException {
        if (userRepo.findUserByPhone(phone) != null) {
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

    private UserDTO entityToDTO(User userEntity) {
        return UserDTO.builder()
                .userId(userEntity.getUserId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .discount(userEntity.getDiscount())
                .build();
    }

    private User fromDTO(UserDTO dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .password(PasswordEncryption.getEncryptedPassword(dto.getPassword()))
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
    }
}
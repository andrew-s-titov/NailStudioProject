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
import org.itrex.entity.enums.RoleType;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;
import org.itrex.repository.data.RecordRepository;
import org.itrex.repository.data.UserRepository;
import org.itrex.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl extends BaseService implements UserService {
    private final UserRepository userRepo;
    private final RecordRepository recordRepo;
    private final UserDTOConverter userDTOConverter;
    private final RoleDTOConverter roleDTOConverter;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public UserResponseDTO getUserById(Long userId) throws DatabaseEntryNotFoundException {
        User user = entityExists(userRepo.findById(userId), "User", userId, "id");
        return userDTOConverter.toUserResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAll(Pageable pageable) {
        Slice<User> slice = userRepo.findAll(pageable);
        if (slice.hasContent()) {
            return slice.getContent().stream()
                    .map(userDTOConverter::toUserResponseDTO)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO user) throws UserExistsException {
        String phone = user.getPhone();
        if (userRepo.findByPhone(phone).isPresent()) {
            throw new UserExistsException(phone);
        }
        User newUser = userDTOConverter.fromUserCreateDTO(user);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        // default role for every registered user
        newUser.getUserRoles().add(Role.builder().roleId(3).roleType(RoleType.CLIENT).build());

        User createdUser = userRepo.save(newUser);
        return userDTOConverter.toUserResponseDTO(createdUser);
    }

    @Override
    public void deleteUser(Long userId) throws DeletingClientWithActiveRecordsException, DatabaseEntryNotFoundException {
        User user = entityExists(userRepo.findById(userId), "User", userId, "id");
        List<Record> records = recordRepo.getByClientUserId(userId);
        if (hasActiveRecords(records)) {
            log.info("Attempt to delete client with active roles");
            throw new DeletingClientWithActiveRecordsException(userId);
        }
        userRepo.delete(user);
    }

    @Override
    public void updateUserInfo(UserUpdateDTO userUpdateDTO) throws DatabaseEntryNotFoundException {
        Long userId = userUpdateDTO.getUserId();
        User user = entityExists(userRepo.findById(userId), "User", userId, "id");
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
            userRepo.save(user);
        }
    }

    @Override
    public void changeClientDiscount(Long clientId, Discount newDiscount) throws DatabaseEntryNotFoundException {
        User user = entityExists(userRepo.findById(clientId), "User (client)", clientId, "id");
        if (!user.getDiscount().equals(newDiscount)) { // avoiding unnecessary request to db
            user.setDiscount(newDiscount);
            userRepo.save(user);
        }
    }

    @Override
    public void addRoleForUser(Long userId, RoleDTO roleDTO)
            throws RoleManagementException, DatabaseEntryNotFoundException {
        User user = entityExists(userRepo.findById(userId), "User", userId, "id");
        Role role = roleDTOConverter.fromRoleDTO(roleDTO);
        Set<Role> userRoles = user.getUserRoles();
        if (userRoles.contains(role)) {
            String message = String.format("Failed to add role: User with id %s already has role %s.",
                    userId, roleDTO.getRoleType().name());
            log.info(message);
            throw new RoleManagementException(message);
        }
        user.getUserRoles().add(role);
        userRepo.save(user);
    }

    @Override
    public void revokeRole(Long userId, RoleDTO roleDTO) throws RoleManagementException, DatabaseEntryNotFoundException {
        User user = entityExists(userRepo.findById(userId), "User", userId, "id");
        Role role = roleDTOConverter.fromRoleDTO(roleDTO);
        Set<Role> userRoles = user.getUserRoles();
        if (userRoles.size() == 1) {
            String message = String.format("Failed to revoke role: User with id %s has only one role", userId);
            log.info(message);
            throw new RoleManagementException(message);
        }
        if (!userRoles.contains(role)) {
            String message = String.format("Failed to revoke role: User with id %s doesn't have role %s.",
                    userId, roleDTO.getRoleType().name());
            log.info(message);
            throw new RoleManagementException(message);
        }
        user.getUserRoles().remove(role);
        userRepo.save(user);
    }

    private boolean hasActiveRecords(List<Record> records) {
        if (!records.isEmpty()) {
            return records.stream()
                    .anyMatch(r -> r.getDate().compareTo(LocalDate.now()) >= 0);
        }
        return false;
    }

    @PostConstruct
    private void firstLaunchCreateAdmin() {
        User admin = User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email("admin@nailstudio.com")
                .phone("375291001010")
                .password(passwordEncoder.encode(password))
                .build();
        admin.getUserRoles().add(Role.builder().roleId(1).roleType(RoleType.ADMIN).build());
        userRepo.save(admin);
    }
}
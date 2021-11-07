package org.itrex.services;

import org.itrex.dto.RecordDTO;
import org.itrex.dto.UserDTO;
import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RecordTime;
import org.itrex.repositories.impl.HibernateUserRepo;
import org.itrex.util.PasswordEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends ServiceWithTransactionWrapping {
    private final HibernateUserRepo userRepo;

    public UserService(@Autowired HibernateUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserDTO findUserById(long id) {
        User userEntity = userRepo.findUserById(id);
        userRepo.closeRepoSession();
        return entityToDTO(userEntity);
    }

    public UserDTO findUserByPhone(String phone) {
        User userEntity = userRepo.findUserByPhone(phone);
        userRepo.closeRepoSession();
        return entityToDTO(userEntity);
    }

    public List<UserDTO> getAll() {
        List<UserDTO> users = userRepo.getAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
        userRepo.closeRepoSession();
        return users;
    }

    public void addUser(UserDTO user) {
        String phone = user.getPhone();
        if (userRepo.findUserByPhone(phone) != null) {
            User userEntity = new User();
            userEntity.setFirstName(user.getFirstName());
            userEntity.setLastName(user.getLastName());
            userEntity.setPhone(phone);
            userEntity.setEmail(user.getEmail());
            userEntity.setPassword(PasswordEncryption.getEncryptedPassword(user.getPassword()));
            userRepo.addUser(userEntity);
            userRepo.closeRepoSession();
        } else {
            throw new IllegalArgumentException("User with the same phone number already exists!");
            // TODO: create exceptions
        }
    }

    public void deleteUser(long id) {
        User userEntity = userRepo.findUserById(id);
        List<Record> records = userEntity.getRecords();
        Date today = Date.valueOf(LocalDate.now());
        long futureRecordsCount = records.stream()
                .filter(r -> r.getDate().compareTo(today) >= 0)
                .count();

        if (records.isEmpty() || futureRecordsCount > 0) {
            throw new IllegalStateException("Cannot delete user with active records!");
            // TODO: create exceptions
        } else {
            doInTransaction(userRepo.getCurrentSession(), () -> userRepo.deleteUser(userEntity));
            userRepo.closeRepoSession();
        }
    }

    public void changeEmail(long userId, String newEmail) {
        User user = userRepo.findUserById(userId);
        doInTransaction(userRepo.getCurrentSession(), () -> userRepo.changeEmail(user, newEmail));
        userRepo.closeRepoSession();
    }

    public void changeDiscount(long userId, Discount discount) {
        User user = userRepo.findUserById(userId);
        doInTransaction(userRepo.getCurrentSession(), () -> userRepo.changeDiscount(user, discount));
        userRepo.closeRepoSession();
    }

    public void addRecordForUser(long userId, RecordDTO record) {
        User userEntity = userRepo.findUserById(userId);
        Record recordEntity = new Record();
        Date date = Date.valueOf(record.getDate());
        RecordTime time = RecordTime.fromString(record.getTime());
        recordEntity.setDate(date);
        recordEntity.setTime(time);
        if (TimeAvailability.getFreeTimeForDate(userRepo.getCurrentSession(), date).contains(time)) {
            throw new IllegalStateException("This time has been already booked and not available!");
            // TODO: ensure booking queue by isolation level
        } else {
            userRepo.addRecordForUser(userEntity, recordEntity);
        }
    }

    public void deleteRecordsForUser(long userId) {
        User user = userRepo.findUserById(userId);
        doInTransaction(userRepo.getCurrentSession(), () -> userRepo.deleteRecordsForUser(user));
        userRepo.closeRepoSession();
    }

    public void addRoleForUser(long userId, Role role) {
        User user = userRepo.findUserById(userId);
        doInTransaction(userRepo.getCurrentSession(), () -> userRepo.addRoleForUser(user, role));
        userRepo.closeRepoSession();
    }

    private UserDTO entityToDTO(User userEntity) {
        UserDTO user = new UserDTO();
        user.setUserId(userEntity.getUserId());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setPhone(userEntity.getPhone());
        user.setEmail(userEntity.getEmail());
        user.setDiscount(userEntity.getDiscount().percentString);
        return user;
    }
}
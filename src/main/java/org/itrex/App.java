package org.itrex;

import org.itrex.config.SpringConfig;
import org.itrex.entities.Record;
import org.itrex.entities.Role;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RecordTime;
import org.itrex.migrationService.FlywayService;
import org.itrex.repositories.impl.HibernateRecordRepo;
import org.itrex.repositories.impl.HibernateRoleRepo;
import org.itrex.repositories.impl.HibernateUserRepo;
import org.itrex.util.HibernateUtil;
import org.itrex.util.PasswordEncryption;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
import java.util.List;

public class App {

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        context.getBean(FlywayService.class);

        HibernateUserRepo userRepo = context.getBean(HibernateUserRepo.class);
        HibernateRecordRepo recordRepo = context.getBean(HibernateRecordRepo.class);
        HibernateRoleRepo roleRepo = context.getBean(HibernateRoleRepo.class);

        List<User> users;
        List<Record> records;

        Role admin = roleRepo.getRoleByName("admin");
        Role staff = roleRepo.getRoleByName("staff");
        Role client = roleRepo.getRoleByName("client");

        User user1 = new User();
        user1.setFirstName("Andrew");
        user1.setLastName("T");
        user1.setPhone("+375293000000");
        user1.setEmail("wow@gmail.com");
        user1.setPassword(PasswordEncryption.getEncryptedPassword("password1"));

        User user2 = new User();
        user2.setFirstName("Boris");
        user2.setLastName("Blade");
        user2.setPhone("+375295000000");
        user2.setEmail("bbritva@mail.ru");
        user2.setPassword(PasswordEncryption.getEncryptedPassword("password2"));

        userRepo.addUser(user1);
        userRepo.addUser(user2);

        userRepo.changeDiscount(user1, Discount.TEN);
        userRepo.changeEmail(user2, "bbritvaNEW@mail.ru");

        userRepo.addRoleForUser(user1, admin);
        userRepo.addRoleForUser(user1, staff);
        userRepo.addRoleForUser(user2, client);

        System.out.println(": : : : : Users after adding and altering : : : : :");
        users = userRepo.selectAll();
        printEntities(users);

        Record record1 = new Record();
        record1.setUser(user1);
        record1.setDate(Date.valueOf("2021-10-18"));
        record1.setTime(RecordTime.SEVENTEEN);

        Record record2 = new Record();
        record2.setUser(user1);
        record2.setDate(Date.valueOf("2021-10-18"));
        record2.setTime(RecordTime.NINE);

        recordRepo.addRecord(record1);
        recordRepo.addRecord(record2);

        System.out.println(": : : : : Records after adding : : : : :");

        records = recordRepo.selectAll();
        printEntities(records);

        recordRepo.deleteRecord(record1);

        userRepo.deleteUser(user1);

        records = recordRepo.selectAll();
        printEntities(records);

        System.out.println(": : : : : Records left: " + records.size() + " : : : : :");

//         * * * close connection * * *
        userRepo.closeRepoSession();
        recordRepo.closeRepoSession();
        roleRepo.closeRepoSession();

        HibernateUtil.closeFactory();
    }

    public static <T> void printEntities(List<T> entities) {
        for (T entity : entities) {
            System.out.println(entity);
        }
    }
}
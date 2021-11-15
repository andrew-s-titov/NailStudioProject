package org.itrex;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.itrex.config.SpringConfig;
import org.itrex.dto.UserDTO;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.repositories.RecordRepo;
import org.itrex.repositories.RoleRepo;
import org.itrex.repositories.UserRepo;
import org.itrex.services.RecordService;
import org.itrex.services.RoleService;
import org.itrex.services.UserService;
import org.itrex.util.PasswordEncryption;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class App {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        context.getBean(Flyway.class);
        SessionFactory sessionFactory = context.getBean(SessionFactory.class);

        UserRepo userRepo = context.getBean(UserRepo.class);
        RoleRepo roleRepo = context.getBean(RoleRepo.class);
        RecordRepo recordRepo = context.getBean(RecordRepo.class);

        UserService userService = context.getBean(UserService.class);
        RecordService recordService = context.getBean(RecordService.class);
        RoleService roleService = context.getBean(RoleService.class);

        UserDTO userDTO1 = UserDTO.builder()
                .firstName("Boris")
                .lastName("Blade")
                .phone("+375295000000")
                .email("bbritva@mail.ru")
                .password("password2")
                .build();

        UserDTO userDTO2 = UserDTO.builder()
                .firstName("Freddy")
                .lastName("Krueger")
                .phone("+375331001010")
                .email("freshmeat@yahoo.com")
                .password("password3")
                .build();

        userService.addUser(userDTO1);
        userService.addUser(userDTO2);

        System.out.println(": : : : : Users after adding and altering : : : : :");
        List<User> users = userRepo.getAll();
        users.forEach(System.out::println);

        recordRepo.getRecordById(5L); // no such record

        User user = User.builder()
                .firstName("A")
                .lastName("B")
                .phone("+37529299999999") // value too long for column
                .email("ccccccc@mail.ru")
                .password(PasswordEncryption.getEncryptedPassword("somePassword"))
                .build();

        userRepo.addUser(user);

//        Record record1 = new Record();
//        record1.setUser(user1);
//        record1.setDate(Date.valueOf("2021-10-18"));
//        record1.setTime(RecordTime.SEVENTEEN);
//
//        Record record2 = new Record();
//        record2.setUser(user1);
//        record2.setDate(Date.valueOf("2021-10-18"));
//        record2.setTime(RecordTime.NINE);
//
//        recordRepo.addRecord(record1);
//        recordRepo.addRecord(record2);
//
//        System.out.println(": : : : : Records after adding : : : : :");
//
//        records = recordRepo.getAll();
//        records.forEach(System.out::println);
//
//        recordRepo.deleteRecord(record1);
//
//        userRepo.deleteUser(user1);
//
//        records = recordRepo.getAll();
//        records.forEach(System.out::println);
//
//        System.out.println(": : : : : Records left: " + records.size() + " : : : : :");

//         * * * close connection * * *
        sessionFactory.close();
    }
}
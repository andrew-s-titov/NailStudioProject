package org.itrex;

import org.h2.jdbcx.JdbcConnectionPool;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RecordTime;
import org.itrex.migrationService.FlywayService;
import org.itrex.repositories.recordRepo.RecordRepo;
import org.itrex.repositories.recordRepo.impl.JdbcRecordRepo;
import org.itrex.repositories.userRepo.UserRepo;
import org.itrex.repositories.userRepo.impl.JdbcUserRepo;

import java.sql.Date;
import java.util.List;

import static org.itrex.dbProperties.H2Properties.*;

public class App {

    public static void main(String[] args) {
        FlywayService flyway = new FlywayService();
        flyway.migrate();

        JdbcConnectionPool jdbcConnectionPool = JdbcConnectionPool.create(DB_URL, DB_USER, DB_PASSWORD);
        UserRepo userRepo = new JdbcUserRepo(jdbcConnectionPool);
        RecordRepo recordRepo = new JdbcRecordRepo(jdbcConnectionPool);

        List<User> users;
        List<Record> records;

        User user1 = new User();
        user1.setFirstName("Andrew");
        user1.setLastName("T");
        user1.setPhone("+375293000000");
        user1.setEmail("wow@gmail.com");

        User user2 = new User();
        user2.setFirstName("Boris");
        user2.setLastName("Blade");
        user2.setPhone("+375295000000");
        user2.setEmail("bbritva@mail.ru");

        userRepo.addUser(user1);
        userRepo.addUser(user2);

        userRepo.changeDiscount(user1, Discount.TEN);

        users = userRepo.selectAll();
        for (User user : users) {
            System.out.println(user);
        }

        Record record1 = new Record();
        record1.setUserId(user2.getUserId());
        record1.setRecordDate(Date.valueOf("2021-10-18"));
        record1.setRecordTime(RecordTime.SEVENTEEN);

        Record record2 = new Record();
        record2.setUserId(user1.getUserId());
        record2.setRecordDate(Date.valueOf("2021-10-18"));
        record2.setRecordTime(RecordTime.NINE);

        recordRepo.addRecord(record1);
        recordRepo.addRecord(record2);


        records = recordRepo.selectAll();
        for (Record record : records) {
            System.out.println(record);
            users.stream()
                    .filter((u) -> u.getUserId() == record.getUserId())
                    .forEach(System.out::println);
        }

        // * * * close connection * * *
        jdbcConnectionPool.dispose();
    }
}
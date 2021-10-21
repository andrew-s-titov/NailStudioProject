package org.itrex;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entities.Record;
import org.itrex.entities.User;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RecordTime;
import org.itrex.migrationService.FlywayService;
import org.itrex.repositories.recordRepo.RecordRepo;
import org.itrex.repositories.recordRepo.impl.HibernateRecordRepo;
import org.itrex.repositories.userRepo.UserRepo;
import org.itrex.repositories.userRepo.impl.HibernateUserRepo;
import org.itrex.util.HibernateUtil;

import java.sql.Date;
import java.util.List;

public class App {

    public static void main(String[] args) {
        FlywayService flyway = new FlywayService();
        flyway.migrate();

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        UserRepo userRepo = new HibernateUserRepo(session);
        RecordRepo recordRepo = new HibernateRecordRepo(session);

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
        userRepo.changeEmail(user2, "bbritvaNEW@mail.ru");

        users = userRepo.selectAll();
        for (User user : users) {
            System.out.println(user);
        }

        Record record1 = new Record();
        record1.setUser(user2);
        record1.setDate(Date.valueOf("2021-10-18"));
        record1.setTime(RecordTime.SEVENTEEN);

        Record record2 = new Record();
        record2.setUser(user2);
        record2.setDate(Date.valueOf("2021-10-18"));
        record2.setTime(RecordTime.NINE);

        recordRepo.addRecord(record1);
        recordRepo.addRecord(record2);

        records = recordRepo.selectAll();
        for (Record record : records) {
            System.out.println(record);
            System.out.println(record.getUser());
        }

        recordRepo.deleteRecord(record1);
        recordRepo.deleteRecord(record2);

        records = recordRepo.selectAll();
        for (Record record : records) {
            System.out.println(record);
            System.out.println(record.getUser());
        }
        System.out.println("Records left: " + records.size());

        // * * * close connection * * *
        session.close();
        HibernateUtil.closeFactory();
    }
}
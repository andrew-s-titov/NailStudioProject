package org.itrex.entities;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.itrex.TestBaseHibernate;
import org.itrex.entities.enums.RecordTime;
import org.junit.jupiter.api.*;

import java.sql.Date;

public class UserTest extends TestBaseHibernate {
    SessionFactory sessionFactory = getContext().getBean(SessionFactory.class);
    private Session session;

    @BeforeEach
    public void openSession() {
        session = sessionFactory.openSession();
    }

    @AfterEach
    public void closeSession() {
        session.close();
    }

    @Test
    @DisplayName("addRecord with valid data - should insert Record into DB, set id and User for this Record")
    public void addRecord() {
        // given
        User user = session.find(User.class, 1L); // this user has 2 records
        Record record = new Record();
        record.setDate(Date.valueOf("2021-11-11"));
        record.setTime(RecordTime.THIRTEEN);

        // when
        Transaction transaction = session.beginTransaction();
        user.addRecord(record);
        transaction.commit();

        // then
        Assertions.assertEquals(3,
                session.createQuery("FROM Record WHERE user_id = 1").getResultList().size());
        Assertions.assertEquals(3, user.getRecords().size());
        Assertions.assertEquals(5, session.createQuery("FROM Record").getResultList().size());
    }

    @Test
    @DisplayName("removeRecord with valid data - should delete Record from DB")
    public void removeRecord() {
        // given
        User user = session.find(User.class, 1L); // this user has 2 records
        Record record = user.getRecords().get(0);

        // when
        Transaction transaction = session.beginTransaction();
        user.removeRecord(record);
        transaction.commit();

        // then
        Assertions.assertEquals(1,
                session.createQuery("FROM Record WHERE user_id = 1").getResultList().size());
        Assertions.assertEquals(1, user.getRecords().size());
        Assertions.assertEquals(3, session.createQuery("FROM Record").getResultList().size());
    }
}

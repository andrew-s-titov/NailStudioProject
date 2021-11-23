package org.itrex.entities;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.itrex.TestBaseHibernate;
import org.itrex.entities.enums.RecordTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest extends TestBaseHibernate {
    private Session session;

    @BeforeEach
    public void openSession() {
        session = getSessionFactory().openSession();
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
        assertEquals(3,
                session.createQuery("FROM Record WHERE user_id = 1").getResultList().size());
        assertEquals(3, user.getRecords().size());
        assertEquals(5, session.createQuery("FROM Record").getResultList().size());
    }
}
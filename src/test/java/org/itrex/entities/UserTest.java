package org.itrex.entities;

import org.hibernate.Transaction;
import org.itrex.TestBaseHibernate;
import org.itrex.entities.enums.RecordTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;

public class UserTest extends TestBaseHibernate {

    public UserTest() {
        super();
    }

    @Test
    @DisplayName("addRecord with valid data - should insert Record into DB, set id and User for this Record")
    public void addRecord() {
        // given
        User user = getSession().find(User.class, 1L); // this user has 2 records
        Record record = new Record();
        record.setDate(Date.valueOf("2021-11-11"));
        record.setTime(RecordTime.THIRTEEN);

        // when
        Transaction transaction = getSession().beginTransaction();
        user.addRecord(record);
        transaction.commit();

        // then
        Assertions.assertEquals(3,
                getSession().createQuery("FROM Record WHERE user_id = 1").getResultList().size());
        Assertions.assertEquals(3, user.getRecords().size());
        Assertions.assertEquals(5, getSession().createQuery("FROM Record").getResultList().size());
    }

    @Test
    @DisplayName("removeRecord with valid data - should delete Record from DB")
    public void removeRecord() {
        // given
        User user = getSession().find(User.class, 1L); // this user has 2 records
        Record record = user.getRecords().get(0);

        // when
        Transaction transaction = getSession().beginTransaction();
        user.removeRecord(record);
        transaction.commit();

        // then
        Assertions.assertEquals(1,
                getSession().createQuery("FROM Record WHERE user_id = 1").getResultList().size());
        Assertions.assertEquals(1, user.getRecords().size());
        Assertions.assertEquals(3, getSession().createQuery("FROM Record").getResultList().size());
    }
}

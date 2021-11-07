package org.itrex.services;

import org.hibernate.Session;
import org.itrex.entities.Record;
import org.itrex.entities.enums.RecordTime;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TimeAvailability {
    public static List<RecordTime> getFreeTimeForDate(Session session, Date date) {
        return session.createQuery("FROM Record WHERE date = :date", Record.class)
                .setParameter("date", date)
                .list()
                .stream()
                .map(Record::getTime)
                .collect(Collectors.toList());
    }
}
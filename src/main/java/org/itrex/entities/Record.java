package org.itrex.entities;

import org.itrex.entities.enums.RecordTime;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "records", schema = "public")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private long recordId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "time", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RecordTime time;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date recordDate) {
        this.date = recordDate;
    }

    public RecordTime getTime() {
        return time;
    }

    public void setTime(RecordTime recordTime) {
        this.time = recordTime;
    }

    @Override
    public String toString() {
        return "* * * Record #" +
                recordId + ": " +
                "user ID #" + userId + ", " +
                date + ", " +
                time.digitsText + " * * *";
    }
}
package org.itrex.entities;

import org.hibernate.annotations.Immutable;
import org.itrex.entities.enums.RecordTime;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "records", schema = "public")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "time", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RecordTime time;

    @Immutable
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "* * * Record #" +
                recordId + ": " +
                "user ID #" + user.getUserId() + ", " +
                date + ", " +
                time.digitsText + " * * *";
    }
}
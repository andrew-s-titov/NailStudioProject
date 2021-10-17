package org.itrex.entities;

import org.itrex.entities.enums.RecordTime;

import java.sql.Date;

public class Record {
    private long recordId;
    private long userId;
    private Date recordDate;
    private RecordTime recordTime;

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

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public RecordTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(RecordTime recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "* * * Record #" +
                recordId + ": " +
                "user ID #" + userId + ", " +
                recordDate + ", " +
                recordTime.digitsText + " * * *";
    }
}
package org.itrex.entity;

import lombok.*;
import org.itrex.entity.enums.RecordTime;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "records", schema = "public")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RecordTime time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private User staff;

    @Override
    public String toString() {
        return "* * * Record #" +
                recordId + ": " +
                "client ID #" + (client == null ? null : client.getUserId()) + ", " +
                "staff ID #" + (staff == null ? null : staff.getUserId()) + ", " +
                date + ", " +
                time.digitsText + " * * *";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(getRecordId(), record.getRecordId()) && getDate().equals(record.getDate()) && getTime() == record.getTime() && Objects.equals(getClient(), record.getClient()) && Objects.equals(getStaff(), record.getStaff());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecordId(), getDate(), getTime(), getClient(), getStaff());
    }
}
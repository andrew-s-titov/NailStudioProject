package org.itrex.entities;

import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.itrex.entities.enums.RecordTime;

import javax.persistence.*;
import java.time.LocalDate;

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
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private User staff;

    @Override
    public String toString() {
        return "* * * Record #" +
                recordId + ": " +
                "user ID #" + client.getUserId() + ", " +
                "staff ID #" + staff.getUserId() + ", " +
                date + ", " +
                time.digitsText + " * * *";
    }
}
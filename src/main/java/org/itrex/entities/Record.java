package org.itrex.entities;

import lombok.*;
import org.hibernate.annotations.Immutable;
import org.itrex.entities.enums.RecordTime;

import javax.persistence.*;
import java.sql.Date;

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
    private Date date;

    @Column(name = "time", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RecordTime time;

    @Immutable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "* * * Record #" +
                recordId + ": " +
                "user ID #" + user.getUserId() + ", " +
                date + ", " +
                time.digitsText + " * * *";
    }
}
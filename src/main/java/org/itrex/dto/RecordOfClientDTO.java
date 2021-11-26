package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entities.enums.RecordTime;

import java.time.LocalDate;

@Data
@Builder
public class RecordOfClientDTO {
    Long recordId;
    LocalDate date;
    RecordTime time;
    String staffFirstName;
    String staffLastName;
}

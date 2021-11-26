package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entity.enums.RecordTime;

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

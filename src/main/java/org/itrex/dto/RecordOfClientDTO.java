package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entity.enums.RecordTime;

import java.time.LocalDate;

@Data
@Builder
public class RecordOfClientDTO {
    private Long recordId;
    private LocalDate date;
    private RecordTime time;
    private String staffFirstName;
    private String staffLastName;
}
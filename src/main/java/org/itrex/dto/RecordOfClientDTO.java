package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entities.enums.RecordTime;

@Data
@Builder
public class RecordOfClientDTO {
    Long recordId;
    String date;
    RecordTime time;
    String staffFirstName;
    String staffLastName;
}

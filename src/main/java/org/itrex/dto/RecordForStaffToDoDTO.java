package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entities.enums.Discount;
import org.itrex.entities.enums.RecordTime;

@Data
@Builder
public class RecordForStaffToDoDTO {
    Long recordId;
    String date;
    RecordTime time;
    String clientFirstName;
    String clientLastName;
    String clientPhone;
    String clientEmail;
    Discount clientDiscount;
}
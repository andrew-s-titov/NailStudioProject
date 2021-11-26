package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entity.enums.Discount;
import org.itrex.entity.enums.RecordTime;

import java.time.LocalDate;

@Data
@Builder
public class RecordForStaffToDoDTO {
    Long recordId;
    LocalDate date;
    RecordTime time;
    String clientFirstName;
    String clientLastName;
    String clientPhone;
    String clientEmail;
    Discount clientDiscount;
}
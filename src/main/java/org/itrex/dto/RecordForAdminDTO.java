package org.itrex.dto;

import lombok.Builder;
import lombok.Data;
import org.itrex.entity.enums.Discount;
import org.itrex.entity.enums.RecordTime;

import java.time.LocalDate;

@Data
@Builder
public class RecordForAdminDTO {
    private Long recordId;
    private LocalDate date;
    private RecordTime time;
    private String clientFirstName;
    private String clientLastName;
    private String clientPhone;
    private String clientEmail;
    private Discount clientDiscount;
    private String staffFirstName;
    private String staffLastName;
}
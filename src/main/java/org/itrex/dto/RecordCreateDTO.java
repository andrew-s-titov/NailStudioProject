package org.itrex.dto;

import lombok.Builder;
import lombok.Data;

import org.itrex.entities.enums.RecordTime;

import java.time.LocalDate;

@Data
@Builder
public class RecordCreateDTO {
    LocalDate date;
    RecordTime time;
    Long userId;
    Long staffId;
}
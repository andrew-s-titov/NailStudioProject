package org.itrex.dto;

import lombok.Builder;
import lombok.Data;

import org.itrex.entities.enums.RecordTime;

@Data
@Builder
public class RecordCreateDTO {
    String date;
    RecordTime time;
    Long userId;
    Long staffId;
}
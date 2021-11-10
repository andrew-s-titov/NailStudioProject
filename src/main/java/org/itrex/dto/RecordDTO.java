package org.itrex.dto;

import lombok.Builder;
import lombok.Data;

import org.itrex.entities.enums.RecordTime;

@Data
@Builder
public class RecordDTO {
    long recordId;
    String date;
    RecordTime time;
    long userId;
}
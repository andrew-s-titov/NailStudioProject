package org.itrex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.itrex.entity.enums.RecordTime;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordCreateDTO {

    @FutureOrPresent
    @NotNull
    LocalDate date;

    @NotNull
    RecordTime time;

    @NotNull
    Long clientId;

    @NotNull
    Long staffId;
}
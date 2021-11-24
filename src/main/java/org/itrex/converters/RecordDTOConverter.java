package org.itrex.converters;

import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entities.Record;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class RecordDTOConverter {
    public RecordForAdminDTO toRecordForAdminDTO(Record recordEntity) {
        return RecordForAdminDTO.builder()
                .recordId(recordEntity.getRecordId())
                .date(recordEntity.getDate().toString())
                .time(recordEntity.getTime())
                .clientFirstName(recordEntity.getClient().getFirstName())
                .clientLastName(recordEntity.getClient().getLastName())
                .clientPhone(recordEntity.getClient().getPhone())
                .clientEmail(recordEntity.getClient().getEmail())
                .clientDiscount(recordEntity.getClient().getDiscount())
                .staffFirstName(recordEntity.getStaff().getFirstName())
                .staffLastName(recordEntity.getStaff().getLastName())
                .build();
    }

    public RecordForStaffToDoDTO toRecordForStaffToDoDTO(Record recordEntity) {
        return RecordForStaffToDoDTO.builder()
                .recordId(recordEntity.getRecordId())
                .date(recordEntity.getDate().toString())
                .time(recordEntity.getTime())
                .clientFirstName(recordEntity.getClient().getFirstName())
                .clientLastName(recordEntity.getClient().getLastName())
                .clientPhone(recordEntity.getClient().getPhone())
                .clientEmail(recordEntity.getClient().getEmail())
                .clientDiscount(recordEntity.getClient().getDiscount())
                .build();
    }

    public RecordOfClientDTO toRecordOfClientDTO(Record recordEntity) {
        return RecordOfClientDTO.builder()
                .recordId(recordEntity.getRecordId())
                .date(recordEntity.getDate().toString())
                .time(recordEntity.getTime())
                .staffFirstName(recordEntity.getStaff().getFirstName())
                .staffLastName(recordEntity.getStaff().getLastName())
                .build();
    }

    public Record fromRecordCreateDTO(RecordCreateDTO recordCreateDTO) {
        return Record.builder()
                .date(Date.valueOf(recordCreateDTO.getDate()))
                .time(recordCreateDTO.getTime())
                .build();
    }
}

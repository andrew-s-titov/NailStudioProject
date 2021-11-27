package org.itrex.converter.impl;

import org.itrex.converter.RecordDTOConverter;
import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.Record;
import org.springframework.stereotype.Component;

@Component
public class RecordDTOConverterImpl implements RecordDTOConverter {
    @Override
    public RecordForAdminDTO toRecordForAdminDTO(Record recordEntity) {
        return RecordForAdminDTO.builder()
                .recordId(recordEntity.getRecordId())
                .date(recordEntity.getDate())
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

    @Override
    public RecordForStaffToDoDTO toRecordForStaffToDoDTO(Record recordEntity) {
        return RecordForStaffToDoDTO.builder()
                .recordId(recordEntity.getRecordId())
                .date(recordEntity.getDate())
                .time(recordEntity.getTime())
                .clientFirstName(recordEntity.getClient().getFirstName())
                .clientLastName(recordEntity.getClient().getLastName())
                .clientPhone(recordEntity.getClient().getPhone())
                .clientEmail(recordEntity.getClient().getEmail())
                .clientDiscount(recordEntity.getClient().getDiscount())
                .build();
    }

    @Override
    public RecordOfClientDTO toRecordOfClientDTO(Record recordEntity) {
        return RecordOfClientDTO.builder()
                .recordId(recordEntity.getRecordId())
                .date(recordEntity.getDate())
                .time(recordEntity.getTime())
                .staffFirstName(recordEntity.getStaff().getFirstName())
                .staffLastName(recordEntity.getStaff().getLastName())
                .build();
    }

    @Override
    public Record fromRecordCreateDTO(RecordCreateDTO recordCreateDTO) {
        return Record.builder()
                .date(recordCreateDTO.getDate())
                .time(recordCreateDTO.getTime())
                .build();
    }
}

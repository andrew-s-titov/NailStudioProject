package org.itrex.converter;

import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.Record;

public interface RecordDTOConverter {

    RecordForAdminDTO toRecordForAdminDTO(Record recordEntity);

    RecordForStaffToDoDTO toRecordForStaffToDoDTO(Record recordEntity);

    RecordOfClientDTO toRecordOfClientDTO(Record recordEntity);

    Record fromRecordCreateDTO(RecordCreateDTO recordCreateDTO);
}

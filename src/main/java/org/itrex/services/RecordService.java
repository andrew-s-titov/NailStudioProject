package org.itrex.services;

import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.exceptions.BookingUnavailableException;

import java.util.List;

public interface RecordService {
    // TODO: remove
    List<RecordForAdminDTO> getAll();

    List<RecordOfClientDTO> getRecordsForUser(Long clientId);

    List<RecordForStaffToDoDTO> getRecordsForStaffToDo(Long staffId);

    Long createRecordForClient(RecordCreateDTO recordCreateDTO) throws BookingUnavailableException;

    void deleteRecord(Long recordId);
}
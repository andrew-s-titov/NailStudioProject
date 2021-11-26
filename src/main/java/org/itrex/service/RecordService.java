package org.itrex.service;

import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.enums.RecordTime;
import org.itrex.exception.BookingUnavailableException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface RecordService {
    List<RecordForAdminDTO> getAll();

    List<RecordOfClientDTO> getRecordsForClient(Long clientId);

    List<RecordForStaffToDoDTO> getRecordsForStaffToDo(Long staffId);

    HashMap<LocalDate, List<RecordTime>> getFreeRecordsFor3MonthsByStaffId(Long staffId);

    Long createRecord(RecordCreateDTO recordCreateDTO) throws BookingUnavailableException;

    void deleteRecord(Long recordId);
}
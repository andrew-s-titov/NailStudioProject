package org.itrex.service;

import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.enums.RecordTime;
import org.itrex.exception.BookingUnavailableException;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RecordService {

    List<RecordForAdminDTO> findAll(Pageable pageable);

    List<RecordOfClientDTO> getRecordsForClient(Long clientId) throws DatabaseEntryNotFoundException;

    List<RecordForStaffToDoDTO> getRecordsForStaffToDo(Long staffId) throws DatabaseEntryNotFoundException;

    Map<LocalDate, List<RecordTime>> getFreeRecordsFor3MonthsByStaffId(Long staffId) throws DatabaseEntryNotFoundException;

    RecordOfClientDTO createRecord(RecordCreateDTO recordCreateDTO)
            throws BookingUnavailableException, DatabaseEntryNotFoundException;

    void deleteRecord(Long recordId) throws DatabaseEntryNotFoundException;
}
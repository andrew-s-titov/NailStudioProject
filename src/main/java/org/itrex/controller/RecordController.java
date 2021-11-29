package org.itrex.controller;

import lombok.RequiredArgsConstructor;
import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.enums.RecordTime;
import org.itrex.exception.BookingUnavailableException;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.service.RecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("records")
public class RecordController {

    private final RecordService recordService;

    // TODO: pagination
    // TODO: restrict use - only for admin
    @GetMapping("/get/all")
    public ResponseEntity<List<RecordForAdminDTO>> getAll() {
        List<RecordForAdminDTO> recordsForAdmin = recordService.getAll();
        return new ResponseEntity<>(recordsForAdmin, HttpStatus.OK);
    }

    @GetMapping("/get/for_client/{clientId}")
    public ResponseEntity<List<RecordOfClientDTO>> getRecordsForClient(@PathVariable Long clientId)
            throws DatabaseEntryNotFoundException {
        List<RecordOfClientDTO> recordsForClient = recordService.getRecordsForClient(clientId);
        return new ResponseEntity<>(recordsForClient, HttpStatus.OK);
    }

    // TODO: restrict use - only for staff and admin
    @GetMapping("/get/for_staff/{staffId}")
    public ResponseEntity<List<RecordForStaffToDoDTO>> getRecordsForStaffToDo(@PathVariable Long staffId)
            throws DatabaseEntryNotFoundException {
        List<RecordForStaffToDoDTO> recordsForStaff = recordService.getRecordsForStaffToDo(staffId);
        return new ResponseEntity<>(recordsForStaff, HttpStatus.OK);
    }

    @GetMapping("/get/free/staff/{staffId}")
    public ResponseEntity<Map<LocalDate, List<RecordTime>>> getFreeRecordsFor3MonthsByStaffId(@PathVariable Long staffId)
            throws DatabaseEntryNotFoundException {
        Map<LocalDate, List<RecordTime>> freeRecords = recordService.getFreeRecordsFor3MonthsByStaffId(staffId);
        return new ResponseEntity<>(freeRecords, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<RecordOfClientDTO> createRecord(@Valid @RequestBody RecordCreateDTO recordCreateDTO)
            throws BookingUnavailableException, DatabaseEntryNotFoundException {
        RecordOfClientDTO createdRecord = recordService.createRecord(recordCreateDTO);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{recordId}")
    public ResponseEntity<Object> deleteRecord(@PathVariable Long recordId) throws DatabaseEntryNotFoundException {
        recordService.deleteRecord(recordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
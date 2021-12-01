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
        return ResponseEntity.ok(recordService.getAll());
    }

    @GetMapping("/get/for_client/{clientId}")
    public ResponseEntity<List<RecordOfClientDTO>> getRecordsForClient(@PathVariable Long clientId)
            throws DatabaseEntryNotFoundException {
        return ResponseEntity.ok(recordService.getRecordsForClient(clientId));
    }

    // TODO: restrict use - only for staff and admin
    @GetMapping("/get/for_staff/{staffId}")
    public ResponseEntity<List<RecordForStaffToDoDTO>> getRecordsForStaffToDo(@PathVariable Long staffId)
            throws DatabaseEntryNotFoundException {
        return ResponseEntity.ok(recordService.getRecordsForStaffToDo(staffId));
    }

    @GetMapping("/get/free/staff/{staffId}")
    public ResponseEntity<Map<LocalDate, List<RecordTime>>> getFreeRecordsFor3MonthsByStaffId(@PathVariable Long staffId)
            throws DatabaseEntryNotFoundException {
        return ResponseEntity.ok(recordService.getFreeRecordsFor3MonthsByStaffId(staffId));
    }

    @PostMapping("/create")
    public ResponseEntity<RecordOfClientDTO> createRecord(@Valid @RequestBody RecordCreateDTO recordCreateDTO)
            throws BookingUnavailableException, DatabaseEntryNotFoundException {
        return ResponseEntity.ok(recordService.createRecord(recordCreateDTO));
    }

    @DeleteMapping("/delete/{recordId}")
    public ResponseEntity<Object> deleteRecord(@PathVariable Long recordId) throws DatabaseEntryNotFoundException {
        recordService.deleteRecord(recordId);
        return ResponseEntity.ok().build();
    }
}
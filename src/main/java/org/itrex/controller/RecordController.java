package org.itrex.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.enums.RecordTime;
import org.itrex.exception.BookingUnavailableException;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.service.RecordService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("records")
@Tag(name = "records", description = "Information about appointments and operations with users")
public class RecordController {

    private final RecordService recordService;

    @Secured("ADMIN")
    @GetMapping("/get/all")
    public ResponseEntity<List<RecordForAdminDTO>> getAll
            (@PageableDefault(size = 50)
             @SortDefault.SortDefaults({
                     @SortDefault(sort = "date", direction = Sort.Direction.ASC),
                     @SortDefault(sort = "time", direction = Sort.Direction.ASC)
             }) Pageable pageable) {
        return ResponseEntity.ok(recordService.findAll(pageable));
    }

    @GetMapping("/get/for_client/{clientId}")
    public ResponseEntity<List<RecordOfClientDTO>> getRecordsForClient(@PathVariable Long clientId)
            throws DatabaseEntryNotFoundException {
        return ResponseEntity.ok(recordService.getRecordsForClient(clientId));
    }

    @Secured({"ADMIN", "STAFF"})
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
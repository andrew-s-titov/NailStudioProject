package org.itrex.controller;

import lombok.RequiredArgsConstructor;
import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.enums.RecordTime;
import org.itrex.exception.BookingUnavailableException;
import org.itrex.service.RecordService;
import org.springframework.http.HttpStatus;
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
    public List<RecordForAdminDTO> getAll() {
        return recordService.getAll();
    }

    @GetMapping("/get/for_client/{clientId}")
    public List<RecordOfClientDTO> getRecordsForClient(@PathVariable Long clientId) {
        return recordService.getRecordsForClient(clientId);
    }

    // TODO: restrict use - only for staff and admin
    @GetMapping("/get/for_staff/{staffId}")
    public List<RecordForStaffToDoDTO> getRecordsForStaffToDo(@PathVariable Long staffId) {
        return recordService.getRecordsForStaffToDo(staffId);
    }

    @GetMapping("/get/free/staff/{staffId}")
    public Map<LocalDate, List<RecordTime>> getFreeRecordsFor3MonthsByStaffId(@PathVariable Long staffId) {
        return recordService.getFreeRecordsFor3MonthsByStaffId(staffId);
    }

    @PostMapping("/create")
    public RecordOfClientDTO createRecord(@Valid @RequestBody RecordCreateDTO recordCreateDTO) throws BookingUnavailableException {
        return recordService.createRecord(recordCreateDTO);
    }

    @DeleteMapping("/delete/{recordId}")
    public HttpStatus deleteRecord(@PathVariable Long recordId) {
        recordService.deleteRecord(recordId);
        return HttpStatus.OK;
    }
}
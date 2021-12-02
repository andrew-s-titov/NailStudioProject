package org.itrex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.converter.RecordDTOConverter;
import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.Record;
import org.itrex.entity.User;
import org.itrex.entity.enums.RecordTime;
import org.itrex.exception.BookingUnavailableException;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.repository.data.RecordRepository;
import org.itrex.repository.data.UserRepository;
import org.itrex.service.RecordService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecordServiceImpl extends BaseService implements RecordService {
    private final RecordRepository recordRepo;
    private final UserRepository userRepo;
    private final RecordDTOConverter converter;

    @Override
    public List<RecordForAdminDTO> findAll(Pageable pageable) {
        Slice<Record> slice = recordRepo.findAll(pageable);
        if (slice.hasContent()) {
            return slice.getContent().stream()
                    .map(converter::toRecordForAdminDTO)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<RecordOfClientDTO> getRecordsForClient(Long clientId) throws DatabaseEntryNotFoundException {
        entityExists(userRepo.findById(clientId), "User (client)", clientId, "id");
        return recordRepo.getByClientUserId(clientId).stream()
                .map(converter::toRecordOfClientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordForStaffToDoDTO> getRecordsForStaffToDo(Long staffId) throws DatabaseEntryNotFoundException {
        entityExists(userRepo.findById(staffId), "User (staff)", staffId, "id");
        return recordRepo.getByStaffUserIdAndDateGreaterThanEqual(staffId, LocalDate.now()).stream()
                .map(converter::toRecordForStaffToDoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RecordOfClientDTO createRecord(RecordCreateDTO recordCreateDTO)
            throws BookingUnavailableException, DatabaseEntryNotFoundException {
        Long clientId = recordCreateDTO.getClientId();
        Long staffId = recordCreateDTO.getStaffId();

        User client = entityExists(userRepo.findById(clientId), "User (client)", clientId, "id");
        User staff = entityExists(userRepo.findById(staffId), "User (staff)", staffId, "id");

        Record newRecord = converter.fromRecordCreateDTO(recordCreateDTO);
        newRecord.setClient(client);
        newRecord.setStaff(staff);

        checkRecordAvailability(recordCreateDTO);

        Record createdRecord = recordRepo.save(newRecord);
        return converter.toRecordOfClientDTO(createdRecord);
    }

    @Override
    public void deleteRecord(Long recordId) throws DatabaseEntryNotFoundException {
        Record record = entityExists(recordRepo.findById(recordId), "Record", recordId, "id");
        recordRepo.delete(record);
    }

    @Override
    public Map<LocalDate, List<RecordTime>> getFreeRecordsFor3MonthsByStaffId(Long staffId) throws DatabaseEntryNotFoundException {
        entityExists(userRepo.findById(staffId), "User (staff)", staffId, "id");

        LocalDate now = LocalDate.now();
        LocalDate lastDate = now.plusMonths(3);

        Map<LocalDate, List<RecordTime>> timeSlotsFor3Months = new TreeMap<>();

        // fill the map with every time slot for every day in 3 months range
        LocalDate startDate = now;
        while (!startDate.equals(lastDate)) {
            timeSlotsFor3Months.put(startDate, new ArrayList<>(Arrays.asList(RecordTime.values())));
            startDate = startDate.plusDays(1);
        }

        // remove booked time slot from the map for staff person
        List<Record> recordsForStaffToDo = recordRepo.getByStaffUserIdAndDateGreaterThanEqual(staffId, LocalDate.now());
        recordsForStaffToDo.stream()
                .filter(r -> timeSlotsFor3Months.containsKey(r.getDate()))
                .forEach(r -> timeSlotsFor3Months.get(r.getDate()).remove(r.getTime()));

        return timeSlotsFor3Months;
    }

    private void checkRecordAvailability(RecordCreateDTO record) throws BookingUnavailableException {
        LocalDate date = record.getDate();
        RecordTime time = record.getTime();
        if (recordRepo.existsByDateIsAndTimeIs(date, time)) {
            throw new BookingUnavailableException(date, time);
        }
    }
}
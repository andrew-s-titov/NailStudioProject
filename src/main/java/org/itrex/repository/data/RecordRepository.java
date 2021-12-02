package org.itrex.repository.data;

import org.itrex.entity.Record;
import org.itrex.entity.enums.RecordTime;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends PagingAndSortingRepository<Record, Long> {
    List<Record> getByClientUserId(Long clientId);

    List<Record> getByStaffUserIdAndDateGreaterThanEqual(Long clientId, LocalDate date);

    boolean existsByDateIsAndTimeIs(LocalDate date, RecordTime time);
}
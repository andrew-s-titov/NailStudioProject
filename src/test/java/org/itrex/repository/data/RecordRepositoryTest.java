package org.itrex.repository.data;

import org.itrex.entity.Record;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class RecordRepositoryTest {

    @Autowired
    RecordRepository recordRepository;

    @Test
    @DisplayName("getByClientUserId with valid data - should return a list of Records for User client")
    public void getByClientUserId1() {
        // given & when
        Long userId = 1L; // User with this id has 2 records
        List<Record> records = recordRepository.getByClientUserId(userId);

        // then
        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("getByClientUserId with valid data - should return a list of Records for User client")
    public void getByClientUserId2() {
        // given & when
        long userId = 100500L; // there are no Users with this id
        List<Record> records = recordRepository.getByClientUserId(userId);

        // then
        assertTrue(records.isEmpty());
    }

    @Test
    @DisplayName("getByStaffUserId with valid data - should return a list of Records for User client")
    public void getByStaffUserId1() {
        // given & when
        Long userId = 1L; // staff with this user_id has 1 records to-do
        List<Record> records = recordRepository.getByStaffUserIdAndDateGreaterThanEqual(userId, LocalDate.of(2021, 11, 01));

        // then
        assertEquals(1, records.size());
    }

    @Test
    @DisplayName("getByStaffUserId with invalid data - should return empty list")
    public void getByStaffUserId2() {
        // given
        long userId = 100500L; // there are no Users with this id
        List<Record> records = recordRepository.getByStaffUserIdAndDateGreaterThanEqual(userId, LocalDate.now());

        // when & then
        assertEquals(0, records.size());
    }
}

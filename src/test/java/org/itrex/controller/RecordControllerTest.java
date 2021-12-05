package org.itrex.controller;

import org.itrex.dto.RecordCreateDTO;
import org.itrex.dto.RecordForAdminDTO;
import org.itrex.dto.RecordForStaffToDoDTO;
import org.itrex.dto.RecordOfClientDTO;
import org.itrex.entity.enums.RecordTime;
import org.itrex.service.RecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RecordControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    RecordService recordService;

    @Test
    @WithUserDetails("+375295055055")
    @DisplayName("getAll - shouldn't throw exceptions, status OK")
    public void getAll() throws Exception {
        // given
        PageRequest defaultPageRequest = PageRequest.of(0, 50, Sort.by("date", "time"));
        PageRequest somePageRequest = PageRequest.of(1, 1, Sort.by("date", "time"));
        List<RecordForAdminDTO> records = Arrays.asList
                (RecordForAdminDTO.builder().recordId(1L).build(), RecordForAdminDTO.builder().recordId(4L).build());
        when(recordService.findAll(defaultPageRequest)).thenReturn(records);
        when(recordService.findAll(somePageRequest))
                .thenReturn(Collections.singletonList(RecordForAdminDTO.builder().recordId(4L).build()));

        // when & then
        MvcResult mvcResult1 = mockMvc.perform(get("/records/get/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(records)))
                .andReturn();

        MvcResult mvcResult2 = mockMvc.perform(get("/records/get/all")
                .param("page", String.valueOf(1))
                .param("size", String.valueOf(1))
                .param("sort", "date,asc")
                .param("sort", "time,asc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(Collections.singletonList(RecordForAdminDTO.builder().recordId(4L).build()))))
                .andReturn();

        assertNull(mvcResult1.getResolvedException());
        assertNull(mvcResult2.getResolvedException());
    }

    @Test
    @WithUserDetails("+375293000000")
    @DisplayName("getRecordsForStaffToDo with valid data - should return json with records, status OK")
    public void getRecordsForStaffToDo() throws Exception {
        // given
        Long staffId = 1L;
        List<RecordForStaffToDoDTO> records = Collections.singletonList(RecordForStaffToDoDTO.builder().recordId(4L).build());
        when(recordService.getRecordsForStaffToDo(staffId)).thenReturn(records);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/records/get/for_staff/" + staffId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(records)))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+18465463222")
    @DisplayName("getRecordsForClient with valid data - should return json with records, status OK")
    public void getRecordsForClient() throws Exception {
        // given
        Long clientId = 1L;
        List<RecordOfClientDTO> records = Collections.singletonList(RecordOfClientDTO.builder().recordId(4L).build());
        when(recordService.getRecordsForClient(clientId)).thenReturn(records);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/records/get/for_client/" + clientId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(records)))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+18465463222")
    @DisplayName("getFreeRecordsFor3MonthsByStaffId with valid data - should return json with data from a map, status OK")
    public void getFreeRecordsFor3MonthsByStaffId() throws Exception {
        // given
        Long staffId = 1L;
        Map<LocalDate, List<RecordTime>> timeSlots = new HashMap<>();
        timeSlots.put(LocalDate.of(2021, 12, 31), Collections.singletonList(RecordTime.NINE));
        when(recordService.getFreeRecordsFor3MonthsByStaffId(staffId)).thenReturn(timeSlots);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/records/get/free/staff/" + staffId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(timeSlots)))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+18465463222")
    @DisplayName("createRecord with valid data - should return json with new Record data, status OK")
    public void createRecord() throws Exception {
        // given
        RecordCreateDTO recordCreateDTO = RecordCreateDTO.builder()
                .date(LocalDate.of(2021, 12, 31))
                .time(RecordTime.NINE)
                .staffId(1L)
                .clientId(2L)
                .build();
        RecordOfClientDTO recordOfClientDTO = RecordOfClientDTO.builder()
                .recordId(5L)
                .date(recordCreateDTO.getDate())
                .time(recordCreateDTO.getTime())
                .staffFirstName("test")
                .staffLastName("test")
                .build();
        when(recordService.createRecord(recordCreateDTO)).thenReturn(recordOfClientDTO);

        // when & then
        MvcResult mvcResult = mockMvc.perform(post("/records/create")
                .content(toJson(recordCreateDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(recordOfClientDTO)))
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }

    @Test
    @WithUserDetails("+18465463222")
    @DisplayName("deleteRecord with valid data - shouldn't throw exception, status OK")
    public void deleteRecord() throws Exception {
        // given
        Long recordId = 1L;
        doNothing().when(recordService).deleteRecord(recordId);

        // when & then
        MvcResult mvcResult = mockMvc.perform(delete("/records/delete/" + recordId))
                .andExpect(status().isOk())
                .andReturn();

        assertNull(mvcResult.getResolvedException());
    }
}

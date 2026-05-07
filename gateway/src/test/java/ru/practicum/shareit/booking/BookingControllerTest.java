package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createShouldReturnOkWhenBookingIsValid() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingClient.create(eq(2L), any(BookingCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "status", "WAITING"
                )));

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 2L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void createShouldReturnBadRequestWhenItemIdIsNull() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 2L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturnBadRequestWhenStartIsInPast() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().minusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 2L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveShouldReturnOk() throws Exception {
        when(bookingClient.approve(1L, 10L, true))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 10,
                        "status", "APPROVED"
                )));

        mockMvc.perform(patch("/bookings/10")
                        .header(USER_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdShouldReturnOk() throws Exception {
        when(bookingClient.getById(1L, 10L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 10,
                        "status", "WAITING"
                )));

        mockMvc.perform(get("/bookings/10")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookingsShouldReturnOk() throws Exception {
        when(bookingClient.getUserBookings(1L, "ALL"))
                .thenReturn(ResponseEntity.ok(java.util.List.of()));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookingsShouldReturnOk() throws Exception {
        when(bookingClient.getOwnerBookings(1L, "ALL"))
                .thenReturn(ResponseEntity.ok(java.util.List.of()));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk());
    }
}
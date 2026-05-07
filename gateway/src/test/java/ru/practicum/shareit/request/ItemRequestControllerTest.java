package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient requestClient;

    @Test
    void createShouldReturnOkWhenDescriptionIsValid() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Нужна дрель");

        when(requestClient.create(eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "description", "Нужна дрель"
                )));

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void createShouldReturnBadRequestWhenDescriptionIsBlank() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnRequestsShouldReturnOk() throws Exception {
        when(requestClient.getOwnRequests(1L))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequestsShouldReturnOk() throws Exception {
        when(requestClient.getAllRequests(1L))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdShouldReturnOk() throws Exception {
        when(requestClient.getById(1L, 10L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 10,
                        "description", "Нужна дрель"
                )));

        mockMvc.perform(get("/requests/10")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk());
    }
}
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void createShouldReturnOkWhenItemIsValid() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Хорошая дрель");
        itemDto.setAvailable(true);

        when(itemClient.create(eq(1L), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "Дрель",
                        "description", "Хорошая дрель",
                        "available", true
                )));

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createShouldReturnBadRequestWhenNameIsBlank() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturnBadRequestWhenAvailableIsNull() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCommentShouldReturnOkWhenTextIsValid() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличная вещь");

        when(itemClient.addComment(eq(1L), eq(2L), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "text", "Отличная вещь"
                )));

        mockMvc.perform(post("/items/2/comment")
                        .header(USER_HEADER, 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addCommentShouldReturnBadRequestWhenTextIsBlank() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        mockMvc.perform(post("/items/2/comment")
                        .header(USER_HEADER, 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}
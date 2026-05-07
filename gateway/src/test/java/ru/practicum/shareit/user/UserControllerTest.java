package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createShouldReturnOkWhenUserIsValid() throws Exception {
        UserDto userDto = new UserDto(null, "Anton", "anton@test.ru");

        when(userClient.create(any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "Anton",
                        "email", "anton@test.ru"
                )));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createShouldReturnBadRequestWhenEmailInvalid() throws Exception {
        UserDto userDto = new UserDto(null, "Anton", "bad-email");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldReturnOk() throws Exception {
        UserDto userDto = new UserDto(null, "New name", null);

        when(userClient.update(eq(1L), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "New name",
                        "email", "anton@test.ru"
                )));

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }
}
package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserClient extends BaseClient {

    private final String serverUrl;

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl + "/users";
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(serverUrl, userDto);
    }

    public ResponseEntity<Object> update(Long userId, UserDto userDto) {
        return patch(serverUrl + "/" + userId, userDto);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get(serverUrl + "/" + userId);
    }

    public ResponseEntity<Object> getAll() {
        return get(serverUrl);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete(serverUrl + "/" + userId);
    }
}
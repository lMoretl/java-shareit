package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
@RequiredArgsConstructor
public class ItemRequestClient {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final RestTemplate restTemplate;

    @Value("${shareit-server.url}")
    private String serverUrl;

    public ResponseEntity<Object> create(Long userId, ItemRequestDto dto) {
        return restTemplate.exchange(
                serverUrl + "/requests",
                HttpMethod.POST,
                new HttpEntity<>(dto, headers(userId)),
                Object.class
        );
    }

    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return restTemplate.exchange(
                serverUrl + "/requests",
                HttpMethod.GET,
                new HttpEntity<>(headers(userId)),
                Object.class
        );
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return restTemplate.exchange(
                serverUrl + "/requests/all",
                HttpMethod.GET,
                new HttpEntity<>(headers(userId)),
                Object.class
        );
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return restTemplate.exchange(
                serverUrl + "/requests/" + requestId,
                HttpMethod.GET,
                new HttpEntity<>(headers(userId)),
                Object.class
        );
    }

    private HttpHeaders headers(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(USER_HEADER, String.valueOf(userId));
        return headers;
    }
}
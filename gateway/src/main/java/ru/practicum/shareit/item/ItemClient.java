package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
@RequiredArgsConstructor
public class ItemClient {

    private final RestTemplate restTemplate;

    @Value("${shareit-server.url}")
    private String serverUrl;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_HEADER, String.valueOf(userId));

        HttpEntity<ItemDto> entity = new HttpEntity<>(itemDto, headers);

        return restTemplate.exchange(
                serverUrl + "/items",
                HttpMethod.POST,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Object> update(Long userId,
                                         Long itemId,
                                         ItemDto itemDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_HEADER, String.valueOf(userId));

        HttpEntity<ItemDto> entity = new HttpEntity<>(itemDto, headers);

        return restTemplate.exchange(
                serverUrl + "/items/" + itemId,
                HttpMethod.PATCH,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_HEADER, String.valueOf(userId));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                serverUrl + "/items/" + itemId,
                HttpMethod.GET,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Object> getAllByOwner(Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_HEADER, String.valueOf(userId));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                serverUrl + "/items",
                HttpMethod.GET,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Object> search(Long userId, String text) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_HEADER, String.valueOf(userId));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                serverUrl + "/items/search?text=" + text,
                HttpMethod.GET,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Object> addComment(Long userId,
                                             Long itemId,
                                             CommentDto commentDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_HEADER, String.valueOf(userId));

        HttpEntity<CommentDto> entity =
                new HttpEntity<>(commentDto, headers);

        return restTemplate.exchange(
                serverUrl + "/items/" + itemId + "/comment",
                HttpMethod.POST,
                entity,
                Object.class
        );
    }
}
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

@Component
@RequiredArgsConstructor
public class BookingClient {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final RestTemplate restTemplate;

    @Value("${shareit-server.url}")
    private String serverUrl;

    public ResponseEntity<Object> create(Long userId, BookingCreateDto dto) {
        HttpHeaders headers = headers(userId);

        return restTemplate.exchange(
                serverUrl + "/bookings",
                HttpMethod.POST,
                new HttpEntity<>(dto, headers),
                Object.class
        );
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
        HttpHeaders headers = headers(userId);

        return restTemplate.exchange(
                serverUrl + "/bookings/" + bookingId + "?approved=" + approved,
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Object.class
        );
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        HttpHeaders headers = headers(userId);

        return restTemplate.exchange(
                serverUrl + "/bookings/" + bookingId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Object.class
        );
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state) {
        HttpHeaders headers = headers(userId);

        return restTemplate.exchange(
                serverUrl + "/bookings?state=" + state,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Object.class
        );
    }

    public ResponseEntity<Object> getOwnerBookings(Long userId, String state) {
        HttpHeaders headers = headers(userId);

        return restTemplate.exchange(
                serverUrl + "/bookings/owner?state=" + state,
                HttpMethod.GET,
                new HttpEntity<>(headers),
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
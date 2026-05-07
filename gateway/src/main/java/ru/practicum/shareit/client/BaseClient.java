package ru.practicum.shareit.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
public class BaseClient {

    protected final RestTemplate restTemplate;

    protected ResponseEntity<Object> get(String url) {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                Object.class
        );
    }

    protected ResponseEntity<Object> post(String url, Object body) {
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, defaultHeaders()),
                Object.class
        );
    }

    protected ResponseEntity<Object> patch(String url, Object body) {
        return restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                new HttpEntity<>(body, defaultHeaders()),
                Object.class
        );
    }

    protected ResponseEntity<Object> delete(String url) {
        return restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(defaultHeaders()),
                Object.class
        );
    }

    protected ResponseEntity<Object> get(String url, Map<String, Object> parameters) {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                Object.class,
                parameters
        );
    }

    protected ResponseEntity<Object> post(String url, Object body, Map<String, Object> parameters) {
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, defaultHeaders()),
                Object.class,
                parameters
        );
    }

    protected ResponseEntity<Object> patch(String url, Object body, Map<String, Object> parameters) {
        return restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                new HttpEntity<>(body, defaultHeaders()),
                Object.class,
                parameters
        );
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
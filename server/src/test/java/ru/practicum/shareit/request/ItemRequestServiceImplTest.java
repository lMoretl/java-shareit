package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createShouldSaveItemRequest() {
        User user = userRepository.save(new User(null, "Anton", "anton@test.ru"));

        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto created = requestService.create(user.getId(), dto);

        assertNotNull(created.getId());
        assertEquals("Нужна дрель", created.getDescription());
        assertNotNull(created.getCreated());
        assertTrue(created.getItems().isEmpty());
    }

    @Test
    void getOwnRequestsShouldReturnOnlyUserRequests() {
        User user = userRepository.save(new User(null, "Anton", "anton2@test.ru"));

        ItemRequestDto first = new ItemRequestDto();
        first.setDescription("Нужен шуруповерт");

        ItemRequestDto second = new ItemRequestDto();
        second.setDescription("Нужна лестница");

        requestService.create(user.getId(), first);
        requestService.create(user.getId(), second);

        Collection<ItemRequestDto> requests = requestService.getOwnRequests(user.getId());

        assertEquals(2, requests.size());
    }
}
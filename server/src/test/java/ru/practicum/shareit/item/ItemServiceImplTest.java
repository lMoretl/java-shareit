package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createShouldSaveItem() {
        User owner = userRepository.save(
                new User(null, "Anton", "anton-item@test.ru")
        );

        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Хорошая дрель");
        dto.setAvailable(true);

        ItemDto created = itemService.create(owner.getId(), dto);

        assertNotNull(created.getId());
        assertEquals("Дрель", created.getName());
        assertEquals("Хорошая дрель", created.getDescription());
        assertTrue(created.getAvailable());
    }

    @Test
    void getOwnerItemsShouldReturnItems() {
        User owner = userRepository.save(
                new User(null, "Anton", "anton-owner@test.ru")
        );

        ItemDto first = new ItemDto();
        first.setName("Шуруповерт");
        first.setDescription("Описание");
        first.setAvailable(true);

        ItemDto second = new ItemDto();
        second.setName("Лестница");
        second.setDescription("Описание");
        second.setAvailable(true);

        itemService.create(owner.getId(), first);
        itemService.create(owner.getId(), second);

        Collection<ItemDto> items =
                itemService.getOwnerItems(owner.getId());

        assertEquals(2, items.size());
    }
}
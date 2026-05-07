package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createShouldSaveBooking() {
        User owner = userRepository.save(
                new User(null, "Owner", "owner@test.com")
        );

        User booker = userRepository.save(
                new User(null, "Booker", "booker@test.com")
        );

        ItemDto item = new ItemDto();
        item.setName("Дрель");
        item.setDescription("Описание");
        item.setAvailable(true);

        ItemDto createdItem = itemService.create(owner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(createdItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking =
                bookingService.create(booker.getId(), bookingCreateDto);

        assertNotNull(createdBooking.getId());
        assertEquals(createdItem.getId(), createdBooking.getItem().getId());
        assertEquals(booker.getId(), createdBooking.getBooker().getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
    }
}
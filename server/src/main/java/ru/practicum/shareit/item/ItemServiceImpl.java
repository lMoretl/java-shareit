package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userService.getUserOrThrow(userId);

        Item item = ItemMapper.toItem(itemDto, owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Запрос не найден"
                    ));
            item.setRequest(request);
        }

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = getItemOrThrow(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Редактировать вещь может только владелец"
            );
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item savedItem = itemRepository.save(existingItem);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = getItemOrThrow(itemId);

        List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(item.getId())
                .stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toDto(item, null, null, comments);
    }

    @Override
    public Collection<ItemDto> getOwnerItems(Long userId) {
        userService.getUserOrThrow(userId);

        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(this::toItemDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository
                .findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(
                        text,
                        text
                )
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userService.getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);

        boolean hasCompletedBooking = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId,
                        userId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                );

        if (!hasCompletedBooking) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Пользователь не арендовал эту вещь"
            );
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return ItemMapper.toCommentDto(savedComment);
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Вещь не найдена"
                ));
    }

    private ItemDto toItemDtoWithDetails(Item item) {
        List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(item.getId())
                .stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toDto(
                item,
                getLastBooking(item.getId()),
                getNextBooking(item.getId()),
                comments
        );
    }

    private BookingDto getLastBooking(Long itemId) {
        return bookingRepository
                .findAllByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                )
                .stream()
                .findFirst()
                .map(BookingMapper::toDto)
                .orElse(null);
    }

    private BookingDto getNextBooking(Long itemId) {
        return bookingRepository
                .findAllByItemIdAndStatusAndStartAfterOrderByStartAsc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                )
                .stream()
                .findFirst()
                .map(BookingMapper::toDto)
                .orElse(null);
    }
}
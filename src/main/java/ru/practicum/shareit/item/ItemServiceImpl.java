package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;

    private final Map<Long, Item> items = new LinkedHashMap<>();
    private long nextId = 1L;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userService.getUserOrThrow(userId);

        Item item = ItemMapper.toItem(itemDto, owner);
        item.setId(nextId++);

        items.put(item.getId(), item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = getItemOrThrow(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Редактировать вещь может только владелец");
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

        return ItemMapper.toDto(existingItem);
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toDto(getItemOrThrow(itemId));
    }

    @Override
    public Collection<ItemDto> getOwnerItems(Long userId) {
        userService.getUserOrThrow(userId);

        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.toLowerCase().trim();

        return items.values().stream()
                .filter(item -> item.getAvailable())
                .filter(item -> containsText(item.getName(), searchText)
                        || containsText(item.getDescription(), searchText))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private Item getItemOrThrow(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не найдена");
        }
        return item;
    }

    private boolean containsText(String source, String text) {
        return source != null && source.toLowerCase().contains(text);
    }
}
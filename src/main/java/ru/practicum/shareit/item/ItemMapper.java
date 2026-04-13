package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        if (itemDto == null) {
            return null;
        }

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequest(null);
        return item;
    }
}
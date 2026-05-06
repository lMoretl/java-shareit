package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        User requestor = userService.getUserOrThrow(userId);

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Описание запроса не может быть пустым");
        }

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        return ItemRequestMapper.toDto(saved, List.of());
    }

    @Override
    public Collection<ItemRequestDto> getOwnRequests(Long userId) {
        userService.getUserOrThrow(userId);

        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(this::toDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(Long userId) {
        userService.getUserOrThrow(userId);

        return requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(this::toDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userService.getUserOrThrow(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Запрос не найден"
                ));

        return toDtoWithItems(request);
    }

    private ItemRequestDto toDtoWithItems(ItemRequest request) {
        List<ItemDto> items = itemRepository.findAllByRequestId(request.getId())
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toDto(request, items);
    }
}
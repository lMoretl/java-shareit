package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new LinkedHashMap<>();
    private long nextId = 1L;

    @Override
    public UserDto create(UserDto userDto) {
        validateEmail(userDto.getEmail(), null);

        User user = UserMapper.toUser(userDto);
        user.setId(nextId++);

        users.put(user.getId(), user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existingUser = getUserOrThrow(userId);

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail(), userId);
            existingUser.setEmail(userDto.getEmail());
        }

        return UserMapper.toDto(existingUser);
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toDto(getUserOrThrow(userId));
    }

    @Override
    public Collection<UserDto> getAll() {
        return users.values().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        getUserOrThrow(userId);
        users.remove(userId);
    }

    @Override
    public User getUserOrThrow(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        return user;
    }

    private void validateEmail(String email, Long currentUserId) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email не может быть пустым");
        }

        boolean emailExists = users.values().stream()
                .anyMatch(user -> user.getEmail() != null
                        && user.getEmail().equalsIgnoreCase(email)
                        && (currentUserId == null || !user.getId().equals(currentUserId)));

        if (emailExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже используется");
        }
    }
}
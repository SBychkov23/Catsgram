package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (users.values().stream().map(user1 -> user1.getEmail()).collect(Collectors.toList()).contains(user.getEmail()))
        {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
    @PutMapping
    public User update(@RequestBody User newUser) throws NotFoundException {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        } else
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
             if (users.values().stream().filter(user ->  !(user.getId() == newUser.getId())
            ).map(user -> user.getEmail()).collect(Collectors.toList()).contains(newUser.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            } //обновляем данные юзера если соблюдены условия
                 if (Optional.ofNullable(newUser.getEmail()).isPresent() )
                     if (!newUser.getEmail().isBlank())
                     oldUser.setEmail(newUser.getEmail());
                 if (Optional.ofNullable(newUser.getPassword()).isPresent()) if
                 (!newUser.getPassword().isBlank())
                     oldUser.setPassword(newUser.getPassword());
                 if (Optional.ofNullable(newUser.getUsername()).isPresent())
                     if(!newUser.getUsername().isBlank())
                     oldUser.setUsername(newUser.getUsername());
                return oldUser;
            }
        else
        throw new NotFoundException("Юзер с id = " + newUser.getId() + " не найден");
    }
}
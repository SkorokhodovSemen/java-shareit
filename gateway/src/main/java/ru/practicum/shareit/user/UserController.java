package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.valid.Create;
import ru.practicum.shareit.valid.Update;
import ru.practicum.shareit.user.dto.UserDto;


@Controller
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUser() {
        log.info("Получен запрос на получение всех пользователей");
        return userClient.getAllUser();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable("userId") long userId) {
        log.info("Получен запрос на получение пользователя по id = {}", userId);
        return userClient.findUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") long userId,
                                             @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с id = {}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("userId") long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        return userClient.deleteUserById(userId);
    }
}


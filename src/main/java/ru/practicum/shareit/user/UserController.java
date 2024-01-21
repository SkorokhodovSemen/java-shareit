package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUser() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUser();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable("userId") long userId) {
        log.info("Получен запрос на получение пользователя по id = {}", userId);
        return userService.findUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с id = {}", userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        userService.deleteUserById(userId);
    }
}

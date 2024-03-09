package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findAllItemRequestForRequestor(@RequestHeader("X-Sharer-User-Id") long idUser) {
        log.info("Получен запрос на поиск всех созданных запросов для пользователя с id = {}", idUser);
        return itemRequestClient.findAllItemRequestForRequestor(idUser);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequest(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на поиск всех запросов для пользователя с id = {}", idUser);
        return itemRequestClient.findAllItemRequest(idUser, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                      @PathVariable("requestId") long requestId) {
        log.info("Получен запрос на поиск запроса с id = {} для пользователя с id = {}", requestId, idUser);
        return itemRequestClient.findItemRequestById(idUser, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                    @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание запроса для вещи от пользователя с id = {}", idUser);
        return itemRequestClient.createItemRequest(idUser, itemRequestDto);
    }
}

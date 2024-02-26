package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> findAllItemRequestForRequestor(@RequestHeader("X-Sharer-User-Id") long idUser) {
        log.info("Получен запрос на поиск всех созданных запросов для пользователя с id = {}", idUser);
        return itemRequestService.findAllItemRequestForRequestor(idUser);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllItemRequest(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                   @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на поиск всех запросов для пользователя с id = {}", idUser);
        return itemRequestService.findAllItemRequest(idUser, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestById(@RequestHeader("X-Sharer-User-Id") long idUser,
                                              @PathVariable("requestId") long requestId) {
        log.info("Получен запрос на поиск запроса с id = {} для пользователя с id = {}", requestId, idUser);
        return itemRequestService.findItemRequestById(idUser, requestId);
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long idUser,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание запроса для вещи от пользователя с id = {}", idUser);
        return itemRequestService.createItemRequest(idUser, itemRequestDto);
    }
}

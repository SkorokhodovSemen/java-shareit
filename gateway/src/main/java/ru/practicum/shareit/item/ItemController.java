package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id") long idUser) {
        log.info("Получен запрос на поиск вещи с id = {}", itemId);
        return itemClient.getItemById(itemId, idUser);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemByOwner(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на получение списка вещей владельца с id = {}", idUser);
        return itemClient.getItemByOwner(idUser, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemForBooker(@RequestParam(name = "text", defaultValue = "") String text,
                                                   @RequestHeader("X-Sharer-User-Id") long idUser,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на поиск вещи в аренду для пользователя с id = {}", idUser);
        return itemClient.getItemForBooker(idUser, from, size, text);
    }

    @PostMapping()
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long idUser,
                                             @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи для пользователя с id = {}", idUser);
        return itemClient.createItem(idUser, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                @Validated(Create.class) @RequestBody CommentDto commentDto,
                                                @PathVariable("itemId") long itemId) {
        log.info("Получен запрос на добавление комментария для вещи с id = {}", itemId);
        return itemClient.createComment(idUser, commentDto, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable("itemId") long itemId) {
        log.info("Получен запрос на обновление вещи с id = {}", itemId);
        return itemClient.updateItem(idUser, itemDto, itemId);
    }
}

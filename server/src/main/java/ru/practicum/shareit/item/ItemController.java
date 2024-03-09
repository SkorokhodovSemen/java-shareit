package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long itemId,
                               @RequestHeader(value = "X-Sharer-User-Id") long idUser) {
        log.info("Получен запрос на поиск вещи с id = {}", itemId);
        return itemService.getItemById(itemId, idUser);
    }

    @GetMapping()
    public List<ItemDto> getItemByOwner(@RequestHeader("X-Sharer-User-Id") long idUser,
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на получение списка вещей владельца с id = {}", idUser);
        return itemService.getItemByOwner(idUser, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemForBooker(@RequestParam(name = "text", defaultValue = "") String text,
                                          @RequestHeader("X-Sharer-User-Id") long idUser,
                                          @RequestParam(name = "from", defaultValue = "0") int from,
                                          @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на поиск вещи в аренду для пользователя с id = {}", idUser);
        return itemService.getItemForBooker(text, idUser, from, size);
    }

    @PostMapping()
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long idUser,
                              @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи для пользователя с id = {}", idUser);
        return itemService.createItem(idUser, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long idUser,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable("itemId") long itemId) {
        log.info("Получен запрос на добавление комментария для вещи с id = {}", itemId);
        return itemService.createComment(idUser, commentDto, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                              @RequestBody ItemDto itemDto,
                              @PathVariable("itemId") long itemId) {
        log.info("Получен запрос на обновление вещи с id = {}", itemId);
        return itemService.updateItem(idUser, itemDto, itemId);
    }
}

package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemByOwner(long userId, int from, int size);

    List<ItemDto> getItemForBooker(String text, long idUser, int from, int size);

    ItemDto createItem(long idUser, ItemDto itemDto);

    ItemDto updateItem(long idUser, ItemDto itemDto, long itemId);

    CommentDto createComment(long idUser, CommentDto commentDto, long itemId);
}

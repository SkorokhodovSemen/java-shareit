package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long itemId);

    List<ItemDto> getItemByOwner(long userId);

    List<ItemDto> getItemForBooker(String text, long idUser);

    ItemDto createItem(long idUser, ItemDto itemDto);

    ItemDto updateItem(long idUser, ItemDto itemDto, long itemId);
}

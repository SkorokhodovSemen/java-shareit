package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getItemById(long itemId);

    List<Item> getItemByOwner(long userId);

    List<Item> getItemForBooker(String text);

    Item createItem(long idUser, Item itemDto);

    Item updateItem(Item itemDto, long itemId);

    Item updateItemFieldDescription(Item itemDto, long itemId);

    Item updateItemFieldName(Item itemDto, long itemId);

    Item updateItemFieldAvailable(Item itemDto, long itemId);
}

package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private long id = 1;
    private final Map<Long, Item> itemRepository = new HashMap<>();
    private final Map<Long, List<Long>> itemRepositoryByOwner = new HashMap<>();

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.get(itemId);
    }

    @Override
    public List<Item> getItemByOwner(long userId) {
        List<Long> itemId = itemRepositoryByOwner.get(userId);
        List<Item> items = new ArrayList<>();
        for (Long id : itemId) {
            items.add(itemRepository.get(id));
        }
        log.info("Найден список вещей пользователя с id = {}", userId);
        return items;
    }

    @Override
    public List<Item> getItemForBooker(String text) {
        List<Item> items = new ArrayList<>();
        for (Item item : itemRepository.values()) {
            if ((item.getName().toLowerCase().contains(text)
                    || item.getDescription().toLowerCase().contains(text)) && item.isAvailable()) {
                items.add(item);
            }
        }
        log.info("Найден список вещей для бронирования для пользователя");
        return items;
    }

    @Override
    public Item createItem(long idUser, Item item) {
        item.setId(id);
        item.setOwner(idUser);
        itemRepository.put(id, item);
        List<Long> itemId;
        if (itemRepositoryByOwner.containsKey(idUser)) {
            itemId = itemRepositoryByOwner.get(idUser);
        } else {
            itemId = new ArrayList<>();
        }
        itemId.add(id);
        itemRepositoryByOwner.put(idUser, itemId);
        id++;
        log.info("Предмет добавлен в репозиторий с id = {}", id - 1);
        return item;
    }

    @Override
    public Item updateItem(Item item, long itemId) {
        itemRepository.get(itemId).setDescription(item.getDescription());
        itemRepository.get(itemId).setName(item.getName());
        itemRepository.get(itemId).setAvailable(item.isAvailable());
        log.info("Предмет с id = {} обновлен", itemId);
        return itemRepository.get(itemId);
    }

    @Override
    public Item updateItemFieldDescription(Item item, long itemId) {
        itemRepository.get(itemId).setDescription(item.getDescription());
        log.info("Поле description у предмета с id = {} обновлено", itemId);
        return itemRepository.get(itemId);
    }

    @Override
    public Item updateItemFieldName(Item item, long itemId) {
        itemRepository.get(itemId).setName(item.getName());
        log.info("Поле name у предмета с id = {} обновлено", itemId);
        return itemRepository.get(itemId);
    }

    @Override
    public Item updateItemFieldAvailable(Item item, long itemId) {
        itemRepository.get(itemId).setAvailable(item.isAvailable());
        log.info("Поле available у предмета с id = {} обновлено", itemId);
        return itemRepository.get(itemId);
    }

    public Map<Long, Item> getItemRepository() {
        return itemRepository;
    }

    public Map<Long, List<Long>> getItemRepositoryByOwner() {
        return itemRepositoryByOwner;
    }
}

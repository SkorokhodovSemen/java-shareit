package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl itemRepository;
    private final UserRepositoryImpl userRepository;

    @Override
    public ItemDto getItemById(long itemId) {
        validateFoundForItem(itemId);
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemByOwner(long userId) {
        validateFoundForUser(userId);
        return itemRepository.getItemByOwner(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemForBooker(String text, long idUser) {
        validateFoundForUser(idUser);
        if (text.equals("Пустое поле")) {
            return new ArrayList<>();
        }
        return itemRepository.getItemForBooker(text.toLowerCase().trim())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(long idUser, ItemDto itemDto) {
        validateFoundForUser(idUser);
        validateForItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.createItem(idUser, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(long idUser, ItemDto itemDto, long itemId) {
        validateFoundForUser(idUser);
        validateFoundForItem(itemId);
        validateForOwner(idUser, itemId);
        if (itemDto.getDescription() != null && itemDto.getName() != null) {
            return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItem(itemDto), itemId));
        }
        if (itemDto.getDescription() != null) {
            return ItemMapper
                    .toItemDto(itemRepository.updateItemFieldDescription(ItemMapper.toItem(itemDto), itemId));
        }
        if (itemDto.getName() != null) {
            return ItemMapper
                    .toItemDto(itemRepository.updateItemFieldName(ItemMapper.toItem(itemDto), itemId));
        }
        return ItemMapper
                .toItemDto(itemRepository.updateItemFieldAvailable(ItemMapper.toItem(itemDto), itemId));
    }

    private void validateFoundForItem(long itemId) {
        if (!itemRepository.getItemRepository().containsKey(itemId)) {
            log.info("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь с данным id не найдена");
        }
    }

    private void validateFoundForUser(long userId) {
        if (!userRepository.getUserRepository().containsKey(userId)) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateForItem(ItemDto itemDto) {
        if (itemDto.getName() == null) {
            log.info("Пользователь не заполнил поле name");
            throw new ValidationException("Пользователь не заполнил поле name");
        }
        if (itemDto.getDescription() == null) {
            log.info("Пользователь не заполнил поле description");
            throw new ValidationException("Пользователь не заполнил поле description");
        }
        if (itemDto.getName().isBlank()) {
            log.info("Пользователь не заполнил поле name");
            throw new ValidationException("Пользователь не заполнил поле name");
        }
        if (itemDto.getDescription().isBlank()) {
            log.info("Пользователь не заполнил поле description");
            throw new ValidationException("Пользователь не заполнил поле description");
        }
        if (!itemDto.isAvailable()) {
            log.info("Пользователь не заполнил поле available");
            throw new ValidationException("Пользователь не заполнил поле available");
        }
    }

    private void validateForOwner(long userId, long itemId) {
        if (!itemRepository.getItemRepositoryByOwner().containsKey(userId)) {
            throw new NotFoundException("У пользователя с id = " + userId + " нет вещи с id = " + itemId);
        }
        if (!itemRepository.getItemRepositoryByOwner().get(userId).contains(itemId)) {
            throw new NotFoundException("У пользователя с id = " + userId + " нет вещи с id = " + itemId);
        }
    }
}

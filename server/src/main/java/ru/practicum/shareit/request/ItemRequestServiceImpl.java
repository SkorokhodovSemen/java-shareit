package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestDto> findAllItemRequestForRequestor(long idUser) {
        userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + idUser + " не найден"));
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestor(idUser);
        Map<Long, List<Item>> itemsForRequest = new HashMap<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.getItemByRequest(itemRequest.getId());
            itemsForRequest.put(itemRequest.getId(), items);
        }
        return itemRequests.stream()
                .map(itemRequest -> {
                    return ItemRequestMapper
                            .toItemRequestDto(itemRequest, toItemDto(itemsForRequest.get(itemRequest.getId())));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllItemRequest(long idUser, int from, int size) {
        userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + idUser + " не найден"));
        validForSizeAndFrom(size, from);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequestor_IdNot(idUser, PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created")))
                .getContent();
        Map<Long, List<Item>> itemsForRequest = new HashMap<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.getItemByRequest(itemRequest.getId());
            itemsForRequest.put(itemRequest.getId(), items);
        }
        return itemRequests.stream()
                .map(itemRequest -> {
                    return ItemRequestMapper
                            .toItemRequestDto(itemRequest, toItemDto(itemsForRequest.get(itemRequest.getId())));
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findItemRequestById(long idUser, long requestId) {
        userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + idUser + " не найден"));
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            log.info("Запроса с id = {} не существует", requestId);
            throw new NotFoundException("Запроса с id = " + requestId + " не существует");
        } else {
            List<Item> items = itemRepository.getItemByRequest(requestId);
            return ItemRequestMapper.toItemRequestDto(itemRequest.get(), toItemDto(items));
        }
    }

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(long idUser, ItemRequestDto itemRequestDto) {
        Optional<User> user = userRepository.findById(idUser);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + idUser + " не найден");
        }
        return ItemRequestMapper.toItemRequestDtoWithoutItem(itemRequestRepository
                .save(ItemRequestMapper.toItemRequest(itemRequestDto, user.get())));
    }

    private List<ItemDto> toItemDto(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();
        if (!items.isEmpty()) {
            itemDtos = items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return itemDtos;
    }

    private void validForSizeAndFrom(int size, int from) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("Проверьте правильность введенных параметров");
        }
    }
}

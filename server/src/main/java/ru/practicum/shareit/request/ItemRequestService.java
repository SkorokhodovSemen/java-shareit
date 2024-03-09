package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> findAllItemRequestForRequestor(long idUser);

    List<ItemRequestDto> findAllItemRequest(long idUser, int from, int size);

    ItemRequestDto findItemRequestById(long idUser, long requestId);

    ItemRequestDto createItemRequest(long idUser, ItemRequestDto itemRequestDto);
}

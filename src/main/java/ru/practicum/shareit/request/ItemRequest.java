package ru.practicum.shareit.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@RequiredArgsConstructor
public class ItemRequest {
    private long id;
    private String description;
    private long requestor;
    private LocalDateTime created;
}

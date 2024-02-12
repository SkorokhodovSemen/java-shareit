package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class ItemOwnerDto extends ItemDto {
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
}

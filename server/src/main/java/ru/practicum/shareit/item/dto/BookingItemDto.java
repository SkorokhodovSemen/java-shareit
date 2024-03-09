package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

@Data
@RequiredArgsConstructor
public class BookingItemDto {
    private long id;
    private long bookerId;
    private Booking booking;
}

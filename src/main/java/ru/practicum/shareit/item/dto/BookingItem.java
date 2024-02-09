package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.Booking;

@Data
@RequiredArgsConstructor
public class BookingItem {
    private long id;
    private long bookerId;
    private Booking booking;
}

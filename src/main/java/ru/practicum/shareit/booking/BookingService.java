package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto getBookingById(long idUser, long bookingId);

    List<BookingDto> getBookingForUser(long idUser, String state, int from, int size);

    List<BookingDto> getBookingForOwner(long idUser, String state, int from, int size);

    BookingDto createBooking(long idUser, BookingDto bookingDto);

    BookingDto approvedBooking(long idUser, long bookingId, boolean isApproved);
}

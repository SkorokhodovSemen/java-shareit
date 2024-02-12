package ru.practicum.shareit.item.model;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

public abstract class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setNextBooking(null);
        itemDto.setLastBooking(null);
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        if (itemDto.getId() != 0) {
            item.setId(itemDto.getId());
        }
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.isAvailable());
        item.setOwner(user);
        return item;
    }

    public static ItemCommentDto toItemCommentDto(ItemOwnerDto itemOwnerDto, List<CommentDto> comments) {
        ItemCommentDto itemCommentDto = new ItemCommentDto();
        itemCommentDto.setComments(comments);
        itemCommentDto.setDescription(itemOwnerDto.getDescription());
        itemCommentDto.setAvailable(itemOwnerDto.isAvailable());
        itemCommentDto.setId(itemOwnerDto.getId());
        itemCommentDto.setLastBooking(itemOwnerDto.getLastBooking());
        itemCommentDto.setNextBooking(itemOwnerDto.getNextBooking());
        itemCommentDto.setName(itemOwnerDto.getName());
        return itemCommentDto;
    }

    public static ItemCommentDto toItemCommentDto(ItemDto itemDto, List<CommentDto> comments) {
        ItemCommentDto itemCommentDto = new ItemCommentDto();
        itemCommentDto.setComments(comments);
        itemCommentDto.setDescription(itemDto.getDescription());
        itemCommentDto.setAvailable(itemDto.isAvailable());
        itemCommentDto.setId(itemDto.getId());
        itemCommentDto.setLastBooking(itemDto.getLastBooking());
        itemCommentDto.setNextBooking(itemDto.getNextBooking());
        itemCommentDto.setName(itemDto.getName());
        return itemCommentDto;
    }

    public static ItemOwnerDto toItemOwnerDto(Item item, List<Booking> bookings) {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setId(item.getId());
        itemOwnerDto.setName(item.getName());
        itemOwnerDto.setDescription(item.getDescription());
        itemOwnerDto.setAvailable(item.isAvailable());
        Optional<Booking> bookingLast = bookings.stream()
                .filter(booking1 -> booking1.getEnd().isBefore(LocalDateTime.now()))
                .max((booking2, booking3) -> booking2.getEnd().compareTo(booking3.getEnd()));
        if (bookingLast.isPresent()) {
            BookingItemDto bookingItemDto = new BookingItemDto();
            bookingItemDto.setBookerId(bookingLast.get().getBooker().getId());
            bookingItemDto.setBooking(bookingLast.get());
            bookingItemDto.setId(bookingLast.get().getId());
            itemOwnerDto.setLastBooking(bookingItemDto);
        } else {
            itemOwnerDto.setLastBooking((BookingItemDto) null);
        }
        Optional<Booking> bookingNext = bookings.stream()
                .filter(booking1 -> booking1.getStart().isAfter(LocalDateTime.now()))
                .min((booking2, booking3) -> booking2.getStart().compareTo(booking3.getStart()));
        if (bookingNext.isPresent()) {
            BookingItemDto bookingItemDto = new BookingItemDto();
            bookingItemDto.setBookerId(bookingNext.get().getBooker().getId());
            bookingItemDto.setBooking(bookingNext.get());
            bookingItemDto.setId(bookingNext.get().getId());
            itemOwnerDto.setNextBooking(bookingItemDto);
        } else {
            itemOwnerDto.setNextBooking((BookingItemDto) null);
        }
        if (bookings.size() == 1 & bookingLast.isEmpty() && bookingNext.isEmpty()) {
            BookingItemDto bookingItemDto = new BookingItemDto();
            Booking booking = bookings.get(0);
            bookingItemDto.setBookerId(booking.getBooker().getId());
            bookingItemDto.setBooking(booking);
            bookingItemDto.setId(booking.getId());
            itemOwnerDto.setLastBooking(bookingItemDto);
        }
        return itemOwnerDto;
    }
}

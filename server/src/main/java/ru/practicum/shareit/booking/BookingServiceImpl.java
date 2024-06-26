package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto getBookingById(long idUser, long bookingId) {
        userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + idUser + " не найден"));
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        validFoundForBooking(bookingOptional, bookingId);
        validFoundForBookerOrOwner(bookingOptional, idUser);
        return BookingMapper.toBookingDto(bookingOptional.get());
    }

    @Override
    public List<BookingDto> getBookingForUser(long idUser, String state, int from, int size) {
        userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + idUser + " не найден"));
        Pageable pageable = PageRequest.of(from / size, size);
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByUser(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case CURRENT:
                return bookingRepository.findCurrentBooking(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case PAST:
                return bookingRepository.findPastBooking(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case FUTURE:
                return bookingRepository.findFutureBooking(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case WAITING:
                return bookingRepository.findWaitingBooking(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case REJECTED:
                return bookingRepository.findRejectedBooking(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getBookingForOwner(long idUser, String state, int from, int size) {
        userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + idUser + " не найден"));
        List<Item> items = itemRepository.findByOwnerWithoutPage(idUser);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByOwner(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case CURRENT:
                return bookingRepository.findCurrentBookingByOwner(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case PAST:
                return bookingRepository.findPastBookingByOwner(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case FUTURE:
                return bookingRepository.findFutureBookingByOwner(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case WAITING:
                return bookingRepository.findWaitingBookingByOwner(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            case REJECTED:
                return bookingRepository.findRejectedBookingByOwner(idUser, pageable)
                        .map(BookingMapper::toBookingDto)
                        .getContent();
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    @Transactional
    public BookingDto createBooking(long idUser, BookingDto bookingDto) {
        Optional<User> userOptional = userRepository.findById(idUser);
        Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());
        validFoundForUser(userOptional, idUser);
        validFoundForItem(bookingDto.getItemId(), itemOptional);
        validForAvailable(itemOptional);
        validForTime(itemOptional, bookingDto);
        validForItemHaveOwner(itemOptional, idUser);
        bookingDto.setBooker(userOptional.get());
        bookingDto.setItem(itemOptional.get());
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBookingCreate(bookingDto)));
    }

    @Override
    @Transactional
    public BookingDto approvedBooking(long idUser, long bookingId, boolean isApproved) {
        userRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + idUser + " не найден"));
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        validFoundForBooking(bookingOptional, bookingId);
        validFoundForBookerOrOwner(bookingOptional, idUser);
        Booking booking = bookingOptional.get();
        validForStatus(booking);
        validForApproveBooker(booking, idUser, isApproved);
        if (isApproved && booking.getItem().getOwner().getId() == idUser) {
            booking.setStatus(Status.APPROVED);
        } else if (booking.getBooker().getId() == idUser) {
            booking.setStatus(Status.CANCELED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    private void validFoundForUser(Optional<User> user, long userId) {
        if (user.isEmpty()) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validFoundForItem(long itemId, Optional<Item> itemOptional) {
        if (itemOptional.isEmpty()) {
            log.info("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь с данным id не найдена");
        }
    }

    private void validFoundForBooking(Optional<Booking> booking, long bookingId) {
        if (booking.isEmpty()) {
            log.info("Бронирование с id = {} не найдено", bookingId);
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    private void validFoundForBookerOrOwner(Optional<Booking> booking, long userId) {
        if (!(booking.get().getBooker().getId() == userId || booking.get().getItem().getOwner().getId() == userId)) {
            log.info("Бронирование для пользователя с id = {} не найдено", userId);
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    private void validForAvailable(Optional<Item> itemOptional) {
        if (!itemOptional.get().isAvailable()) {
            log.info("Создание бронирования невозможно, вещь недоступна");
            throw new ValidationException("Вещь недоступна");
        }
    }

    private void validForTime(Optional<Item> itemOptional, BookingDto bookingDto) {
        List<Booking> bookings = bookingRepository.findBookingByItemToFree(itemOptional.get().getId(),
                bookingDto.getStart(), bookingDto.getEnd());
        if (bookings.size() > 0) {
            log.info("Создание бронирования невозможно, вещь занята");
            throw new ValidationException("Вещь занята");
        }
    }

    private void validForStatus(Booking booking) {
        if (!booking.getStatus().equals(Status.WAITING)) {
            log.info("Статус у данного бронирования уже изменен");
            throw new ValidationException("Статус у данного бронирования уже изменен");
        }
    }

    private void validForApproveBooker(Booking booking, long idUser, boolean isApprove) {
        if (isApprove && booking.getBooker().getId() == idUser) {
            log.info("Пользователь с id = {} не может одобрить данный запрос с id = {}", booking.getId(), idUser);
            throw new NotFoundException("Вы не можете одобрить данный запрос");
        }
    }

    private void validForItemHaveOwner(Optional<Item> itemOptional, long idUser) {
        if (itemOptional.get().getOwner().getId() == idUser) {
            log.info("Пользователь с id = {} не может создать бронирование на свою вещь", idUser);
            throw new NotFoundException("Вы не можете создать бронирование на свою вещь");
        }
    }
}

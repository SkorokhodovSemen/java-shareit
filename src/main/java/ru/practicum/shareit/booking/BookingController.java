package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long idUser,
                                     @PathVariable("bookingId") long bookingId) {
        log.info("Получен запрос на поиск бронирования для пользователя с id = {}", idUser);
        return bookingService.getBookingById(idUser, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getBookingForUser(@RequestHeader("X-Sharer-User-Id") long idUser,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestParam(name = "from", defaultValue = "0") int from,
                                              @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на получение информации о бронированиях пользователя с id = {}", idUser);
        return bookingService.getBookingForUser(idUser, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingForOwner(@RequestHeader("X-Sharer-User-Id") long idUser,
                                               @RequestParam(name = "state", defaultValue = "ALL") String state,
                                               @RequestParam(name = "from", defaultValue = "0") int from,
                                               @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на получение информации о статусе вещей владельца с id = {}", idUser);
        return bookingService.getBookingForOwner(idUser, state, from, size);
    }

    @PostMapping()
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long idUser,
                                    @Valid @RequestBody BookingDto bookingDto) {
        log.info("Добавлен запрос на создание бронирование вещи для пользователя с id = {}", idUser);
        return bookingService.createBooking(idUser, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader("X-Sharer-User-Id") long idUser,
                                      @PathVariable("bookingId") long bookingId,
                                      @RequestParam(name = "approved") boolean isApproved) {
        log.info("Добавлен запрос на подтверждение бронирования с id = {}", bookingId);
        return bookingService.approvedBooking(idUser, bookingId, isApproved);
    }
}

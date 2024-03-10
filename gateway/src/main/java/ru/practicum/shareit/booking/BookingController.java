package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.valid.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                 @PathVariable("bookingId") long bookingId) {
        log.info("Получен запрос на поиск бронирования для пользователя с id = {}", idUser);
        return bookingClient.getBookingById(idUser, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getBookingForUser(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на получение информации о бронированиях пользователя с id = {}", idUser);
        return bookingClient.getBookingForUser(idUser, from, size, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingForOwner(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получен запрос на получение информации о статусе вещей владельца с id = {}", idUser);
        return bookingClient.getBookingForOwner(idUser, from, size, state);
    }

    @PostMapping()
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        log.info("Добавлен запрос на создание бронирование вещи для пользователя с id = {}", idUser);
        return bookingClient.createBooking(idUser, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                  @PathVariable("bookingId") long bookingId,
                                                  @RequestParam(name = "approved") boolean isApproved) {
        log.info("Добавлен запрос на подтверждение бронирования с id = {}", bookingId);
        return bookingClient.approvedBooking(idUser, bookingId, isApproved);
    }
}

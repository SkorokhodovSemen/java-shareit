package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    ItemDto itemDto1 = new ItemDto();
    ItemDto itemDto2 = new ItemDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("test");
        userDto1.setEmail("test@test.ru");
        userDto2.setName("update");
        userDto2.setEmail("update@update.ru");
        itemDto1.setAvailable(true);
        itemDto1.setDescription("test");
        itemDto1.setName("test");
        itemDto2.setAvailable(true);
        itemDto2.setDescription("update");
        itemDto2.setName("update");
    }

    @Test
    void getBookingForOwner() throws Exception {
        UserDto userDto = userService.createUser(userDto1);
        UserDto userDtoTest = userService.createUser(userDto2);
        ItemDto itemDto = itemService.createItem(userDto.getId(), itemDto1);
        ItemDto itemDtoTest = itemService.createItem(userDtoTest.getId(), itemDto2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoTest.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoTest = bookingService.createBooking(userDto.getId(), bookingDto);
        bookingService.approvedBooking(userDtoTest.getId(), bookingDtoTest.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.item.owner.id = :id", Booking.class);
        List<Booking> bookings = query.setParameter("id", userDtoTest.getId()).getResultList();
        List<BookingDto> bookingDtos = bookingService
                .getBookingForOwner(userDtoTest.getId(), "ALL", 0, 1);
        assertThat(bookings.size(), equalTo(bookingDtos.size()));
        assertThat(bookings.get(0).getId(), equalTo(bookingDtos.get(0).getId()));
        List<BookingDto> bookingDtos2 = bookingService
                .getBookingForOwner(userDtoTest.getId(), "CURRENT", 0, 1);
        assertThat(bookingDtos2.size(), equalTo(0));
        List<BookingDto> bookingDtos3 = bookingService
                .getBookingForOwner(userDtoTest.getId(), "PAST", 0, 1);
        assertThat(bookingDtos3.size(), equalTo(bookings.size()));
        List<BookingDto> bookingDtos4 = bookingService
                .getBookingForOwner(userDtoTest.getId(), "FUTURE", 0, 1);
        assertThat(bookingDtos4.size(), equalTo(0));
        List<BookingDto> bookingDtos5 = bookingService
                .getBookingForOwner(userDtoTest.getId(), "WAITING", 0, 1);
        assertThat(bookingDtos5.size(), equalTo(0));
        List<BookingDto> bookingDtos6 = bookingService
                .getBookingForOwner(userDtoTest.getId(), "REJECTED", 0, 1);
        assertThat(bookingDtos6.size(), equalTo(0));
    }

    @Test
    void getBookingForUserAndGetBookingById() throws Exception {
        UserDto userDto = userService.createUser(userDto1);
        UserDto userDtoTest = userService.createUser(userDto2);
        ItemDto itemDto = itemService.createItem(userDto.getId(), itemDto1);
        ItemDto itemDtoTest = itemService.createItem(userDtoTest.getId(), itemDto2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoTest.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(100));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(200));
        BookingDto bookingDtoTest = bookingService.createBooking(userDto.getId(), bookingDto);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE ((b.booker.id = :id) AND (b.status = 'WAITING'))",
                        Booking.class);
        List<Booking> bookings = query.setParameter("id", userDto.getId()).getResultList();
        List<BookingDto> bookingDtos = bookingService
                .getBookingForUser(userDto.getId(), "WAITING", 0, 1);
        assertThat(bookings.size(), equalTo(bookingDtos.size()));
        assertThat(bookings.get(0).getId(), equalTo(bookingDtos.get(0).getId()));
        BookingDto bookingDto1 = bookingService.getBookingById(userDto.getId(), bookingDtoTest.getId());
        TypedQuery<Booking> query1 =
                em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking booking = query1.setParameter("id", bookingDtoTest.getId()).getSingleResult();
        assertThat(booking.getId(), equalTo(bookingDto1.getId()));
        assertThat(booking.getBooker().getId(), equalTo(bookingDto1.getBooker().getId()));
        List<BookingDto> bookingDtos2 = bookingService
                .getBookingForUser(userDtoTest.getId(), "CURRENT", 0, 1);
        assertThat(bookingDtos2.size(), equalTo(0));
        List<BookingDto> bookingDtos3 = bookingService
                .getBookingForUser(userDtoTest.getId(), "PAST", 0, 1);
        assertThat(bookingDtos3.size(), equalTo(0));
        List<BookingDto> bookingDtos4 = bookingService
                .getBookingForUser(userDtoTest.getId(), "FUTURE", 0, 1);
        assertThat(bookingDtos4.size(), equalTo(0));
        List<BookingDto> bookingDtos5 = bookingService
                .getBookingForUser(userDtoTest.getId(), "WAITING", 0, 1);
        assertThat(bookingDtos5.size(), equalTo(0));
        List<BookingDto> bookingDtos6 = bookingService
                .getBookingForUser(userDtoTest.getId(), "REJECTED", 0, 1);
        assertThat(bookingDtos6.size(), equalTo(0));
    }

    @Test
    void catchException() {
        UserDto userDto = userService.createUser(userDto1);
        UserDto userDtoTest = userService.createUser(userDto2);
        ItemDto itemDto = itemService.createItem(userDto.getId(), itemDto1);
        ItemDto itemDtoTest = itemService.createItem(userDtoTest.getId(), itemDto2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoTest.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(100));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(200));
        BookingDto bookingDtoTest = bookingService.createBooking(userDto.getId(), bookingDto);
        try {
            bookingService.createBooking(100, bookingDto);
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), equalTo("Пользователь не найден"));
        }
        try {
            bookingService.getBookingForOwner(1, "OLL", 0, 1);
        } catch (ValidationException e) {
            assertThat(e.getMessage(), equalTo("Unknown state: UNSUPPORTED_STATUS"));
        }
        try {
            BookingDto bookingDto1 = new BookingDto();
            bookingDto1.setStart(LocalDateTime.now().minusDays(1));
            bookingDto1.setEnd(LocalDateTime.now().plusSeconds(5));
            bookingDto1.setItemId(1);
            bookingService.createBooking(1, bookingDto1);
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    equalTo("Неверные параметры для времени, проверьте правильность запроса"));
        }
        try {
            BookingDto bookingDto1 = new BookingDto();
            bookingDto1.setStart(LocalDateTime.now().plusSeconds(2));
            bookingDto1.setEnd(LocalDateTime.now().plusSeconds(5));
            bookingDto1.setItemId(99);
            bookingService.createBooking(1, bookingDto1);
        } catch (NotFoundException e) {
            assertThat(e.getMessage(),
                    equalTo("Вещь с данным id не найдена"));
        }
        try {
            ItemDto itemDto3 = new ItemDto();
            itemDto3.setAvailable(false);
            itemDto3.setName("test");
            itemDto3.setDescription("test");
            ItemDto itemDto4 = itemService.createItem(userDtoTest.getId(), itemDto3);
            BookingDto bookingDto1 = new BookingDto();
            bookingDto1.setStart(LocalDateTime.now().plusSeconds(2));
            bookingDto1.setEnd(LocalDateTime.now().plusSeconds(5));
            bookingDto1.setItemId(itemDto4.getId());
            bookingService.createBooking(1, bookingDto1);
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    equalTo("Вещь недоступна"));
        }
        try {
            ItemDto itemDto3 = new ItemDto();
            itemDto3.setAvailable(true);
            itemDto3.setName("test");
            itemDto3.setDescription("test");
            ItemDto itemDto4 = itemService.createItem(userDtoTest.getId(), itemDto3);
            BookingDto bookingDto1 = new BookingDto();
            bookingDto1.setStart(LocalDateTime.now().plusSeconds(100));
            bookingDto1.setEnd(LocalDateTime.now().plusSeconds(200));
            bookingDto1.setItemId(itemDto4.getId());
            bookingService.createBooking(1, bookingDto1);
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    equalTo("Вещь занята"));
        }
        try {
            bookingService.approvedBooking(userDtoTest.getId(), bookingDtoTest.getId(), true);
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    equalTo("Статус у данного бронирования уже изменен"));
        }
        try {
            ItemDto itemDto3 = new ItemDto();
            itemDto3.setAvailable(true);
            itemDto3.setName("test");
            itemDto3.setDescription("test");
            ItemDto itemDto4 = itemService.createItem(userDtoTest.getId(), itemDto3);
            BookingDto bookingDto1 = new BookingDto();
            bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
            bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
            bookingDto1.setItemId(itemDto4.getId());
            bookingService.createBooking(userDtoTest.getId(), bookingDto1);
        } catch (NotFoundException e) {
            assertThat(e.getMessage(),
                    equalTo("Вы не можете создать бронирование на свою вещь"));
        }
        try {
            ItemDto itemDto3 = new ItemDto();
            itemDto3.setAvailable(true);
            itemDto3.setName("test");
            itemDto3.setDescription("test");
            ItemDto itemDto4 = itemService.createItem(userDto.getId(), itemDto3);
            UserDto userDto4 = new UserDto();
            userDto4.setName("testtest");
            userDto4.setEmail("e@e.ru");
            UserDto userDto3 = userService.createUser(userDto4);
            BookingDto bookingDto1 = new BookingDto();
            bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
            bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
            bookingDto1.setItemId(itemDto4.getId());
            bookingService.createBooking(userDto3.getId(), bookingDto1);
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    equalTo("Вы не можете одобрить данный запрос"));
        }
    }
}

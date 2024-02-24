package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    LocalDateTime end;
    LocalDateTime start;

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
    void getBookingForOwnerWithOtherParameters() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(itemDtoCreate2.getId());
        bookingDto1.setStart(start);
        bookingDto1.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto1);
        //Подтверждение бронирования владельцем вещи
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        //Ожидание завершения бронирования
        TimeUnit.SECONDS.sleep(4);
        //Поиск броинрования в бд по владельцу вещи
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.item.owner.id = :id", Booking.class);
        List<Booking> bookings = query.setParameter("id", userDtoCreate2.getId()).getResultList();
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos1 = bookingService
                .getBookingForOwner(userDtoCreate2.getId(), "ALL", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookings.size(), equalTo(bookingDtos1.size()));
        assertThat(bookings.get(0).getId(), equalTo(bookingDtos1.get(0).getId()));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos2 = bookingService
                .getBookingForOwner(userDtoCreate2.getId(), "PAST", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookings.size(), equalTo(bookingDtos2.size()));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos3 = bookingService
                .getBookingForOwner(userDtoCreate2.getId(), "CURRENT", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookingDtos3.size(), equalTo(0));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos4 = bookingService
                .getBookingForOwner(userDtoCreate2.getId(), "FUTURE", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookingDtos4.size(), equalTo(0));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos5 = bookingService
                .getBookingForOwner(userDtoCreate2.getId(), "WAITING", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookingDtos5.size(), equalTo(0));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos6 = bookingService
                .getBookingForOwner(userDtoCreate2.getId(), "REJECTED", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookingDtos6.size(), equalTo(0));
        //Попытка нахождения с неправильными from and size
        ValidationException validationExceptionIncorrectState = assertThrows(ValidationException.class,
                () -> bookingService.getBookingForOwner(userDtoCreate2.getId(), "OLL", 0, 1));
        assertThat(validationExceptionIncorrectState.getMessage(),
                equalTo("Unknown state: UNSUPPORTED_STATUS"));
        ValidationException validationExceptionIncorrectParameters = assertThrows(ValidationException.class,
                () -> bookingService.getBookingForOwner(userDtoCreate2.getId(), "ALL", -1, 1));
        assertThat(validationExceptionIncorrectParameters.getMessage(),
                equalTo("Проверьте правильность введенных параметров"));
    }

    @Test
    void createBookingWithIncorrectTime() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().minusDays(2);
        start = LocalDateTime.now().minusDays(3);
        BookingDto bookingDtoWrong = new BookingDto();
        bookingDtoWrong.setItemId(itemDtoCreate2.getId());
        bookingDtoWrong.setStart(start);
        bookingDtoWrong.setEnd(end);
        //Попытка создать бронирование с неправильным времененем
        ValidationException validationExceptionIncorrectTime = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(userDtoCreate1.getId(), bookingDtoWrong));
        assertThat(validationExceptionIncorrectTime.getMessage(),
                equalTo("Неверные параметры для времени, проверьте правильность запроса"));
    }

    @Test
    void createBookingWithIncorrectUserId() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        //Попытка создать бронирование с неправильным id пользователя
        NotFoundException notFoundExceptionIncorrectUserId = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(userDtoCreate1.getId() + 5, bookingDto));
        assertThat(notFoundExceptionIncorrectUserId.getMessage(),
                equalTo("Пользователь не найден"));
    }

    @Test
    void createBookingWithIncorrectItemId() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId() + 5);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        //Попытка создать бронирование с неправильным id вещи
        NotFoundException notFoundExceptionIncorrectItemId = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(userDtoCreate1.getId(), bookingDto));
        assertThat(notFoundExceptionIncorrectItemId.getMessage(),
                equalTo("Вещь с данным id не найдена"));
    }

    @Test
    void createBookingWithNotAvailableItem() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        //Изменение доступности вещи
        itemDto2.setAvailable(false);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        //Попытка создать бронирование с недоступной вещью
        ValidationException validationExceptionIncorrectTime = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(userDtoCreate1.getId(), bookingDto));
        assertThat(validationExceptionIncorrectTime.getMessage(),
                equalTo("Вещь недоступна"));
    }

    @Test
    void createBookingWithBookedItem() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования 1
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(itemDtoCreate2.getId());
        bookingDto1.setStart(start);
        bookingDto1.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto1);
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        //Создание бронирования 2
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setItemId(itemDtoCreate2.getId());
        bookingDto2.setStart(start);
        bookingDto2.setEnd(end);
        //Попытка создать бронирование с занятой вещью
        ValidationException validationExceptionIncorrectTime = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(userDtoCreate1.getId(), bookingDto2));
        assertThat(validationExceptionIncorrectTime.getMessage(),
                equalTo("Вещь занята"));
    }

    @Test
    void createBookingWithOwnerId() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        //Попытка создать бронирование с неправильным времененем
        NotFoundException notFoundExceptionWithOwnerId = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(userDtoCreate2.getId(), bookingDto));
        assertThat(notFoundExceptionWithOwnerId.getMessage(),
                equalTo("Вы не можете создать бронирование на свою вещь"));
    }

    @Test
    void approvedBooking() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto);
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.status = :status", Booking.class);
        List<Booking> bookings = query.setParameter("status", Status.APPROVED).getResultList();
        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void cancelledBooking() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto);
        //Отмена бронирования букером
        bookingService.approvedBooking(userDtoCreate1.getId(), bookingDtoCreate1.getId(), false);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.status = :status", Booking.class);
        List<Booking> bookings = query.setParameter("status", Status.CANCELED).getResultList();
        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void rejectedBooking() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto);
        //Отмена бронирования владельцем
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), false);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.status = :status", Booking.class);
        List<Booking> bookings = query.setParameter("status", Status.REJECTED).getResultList();
        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void approvedBookingWithWrongUserId() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto);
        //Попытка подтверждения бронирования неизвестным пользователем
        NotFoundException validationExceptionIncorrectUserId = assertThrows(NotFoundException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate2.getId() + 5, bookingDtoCreate1.getId(), true));
        assertThat(validationExceptionIncorrectUserId.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate2.getId() + 5) + " не найден"));
    }

    @Test
    void approvedBookingWithWrongBookingId() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto);
        //Попытка подтверждения бронирования с неправильным id бронирования
        NotFoundException validationExceptionIncorrectBookingId = assertThrows(NotFoundException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId() + 5, true));
        assertThat(validationExceptionIncorrectBookingId.getMessage(),
                equalTo("Бронирование не найдено"));
    }

    @Test
    void approvedBookingWithDoubleTime() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto);
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        //Попытка подтверждения бронирования второй раз
        ValidationException validationExceptionDoubleTime = assertThrows(ValidationException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true));
        assertThat(validationExceptionDoubleTime.getMessage(),
                equalTo("Статус у данного бронирования уже изменен"));
    }

    @Test
    void approvedBookingWithIncorrectUserId() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        UserDto userDto3 = new UserDto();
        userDto3.setName("test3");
        userDto3.setEmail("test3@test3.ru");
        UserDto userDtoCreate3 = userService.createUser(userDto3);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto);
        //Попытка подтверждения бронирования неизвестным пользователем
        NotFoundException validationExceptionIncorrectBookingId = assertThrows(NotFoundException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate3.getId(), bookingDtoCreate1.getId(), true));
        assertThat(validationExceptionIncorrectBookingId.getMessage(),
                equalTo("Бронирование не найдено"));
    }

    @Test
    void approvedBookingWithBookerId() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto);
        //Попытка подтверждения бронирования неизвестным пользователем
        NotFoundException validationExceptionWithBookerId = assertThrows(NotFoundException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate1.getId(), bookingDtoCreate1.getId(), true));
        assertThat(validationExceptionWithBookerId.getMessage(),
                equalTo("Вы не можете одобрить данный запрос"));
    }

    @Test
    void getBookingForUserWithOtherParameters() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(itemDtoCreate2.getId());
        bookingDto1.setStart(start);
        bookingDto1.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto1);
        //Подтверждение бронирования владельцем вещи
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        //Ожидание завершения бронирования
        TimeUnit.SECONDS.sleep(4);
        //Поиск бронирования в бд по букеру
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.booker.id = :id", Booking.class);
        List<Booking> bookings = query.setParameter("id", userDtoCreate1.getId()).getResultList();
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos1 = bookingService
                .getBookingForUser(userDtoCreate1.getId(), "ALL", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookings.size(), equalTo(bookingDtos1.size()));
        assertThat(bookings.get(0).getId(), equalTo(bookingDtos1.get(0).getId()));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos2 = bookingService
                .getBookingForUser(userDtoCreate1.getId(), "PAST", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookings.size(), equalTo(bookingDtos2.size()));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos3 = bookingService
                .getBookingForUser(userDtoCreate1.getId(), "CURRENT", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookingDtos3.size(), equalTo(0));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos4 = bookingService
                .getBookingForUser(userDtoCreate1.getId(), "FUTURE", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookingDtos4.size(), equalTo(0));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos5 = bookingService
                .getBookingForUser(userDtoCreate1.getId(), "WAITING", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookingDtos5.size(), equalTo(0));
        //Получение бронирования через метод сервиса
        List<BookingDto> bookingDtos6 = bookingService
                .getBookingForUser(userDtoCreate1.getId(), "REJECTED", 0, 1);
        //Проверка правильности нахождения бронирования
        assertThat(bookingDtos6.size(), equalTo(0));
        //Попытка нахождения с неправильными from and size
        ValidationException validationExceptionIncorrectState = assertThrows(ValidationException.class,
                () -> bookingService.getBookingForUser(userDtoCreate1.getId(), "OLL", 0, 1));
        assertThat(validationExceptionIncorrectState.getMessage(),
                equalTo("Unknown state: UNSUPPORTED_STATUS"));
        ValidationException validationExceptionIncorrectParameters = assertThrows(ValidationException.class,
                () -> bookingService.getBookingForUser(userDtoCreate1.getId(), "ALL", -1, 1));
        assertThat(validationExceptionIncorrectParameters.getMessage(),
                equalTo("Проверьте правильность введенных параметров"));
    }

    @Test
    void getBookingById() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(itemDtoCreate2.getId());
        bookingDto1.setStart(start);
        bookingDto1.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto1);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking bookings = query.setParameter("id", bookingDtoCreate1.getId()).getSingleResult();
        BookingDto bookingDto = bookingService.getBookingById(userDtoCreate1.getId(), bookingDtoCreate1.getId());
        assertThat(bookings.getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void getBookingByIdWrongIdUser() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        UserDto userDto3 = new UserDto();
        userDto3.setName("test3");
        userDto3.setEmail("test3@test3.ru");
        UserDto userDtoCreate3 = userService.createUser(userDto3);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(itemDtoCreate2.getId());
        bookingDto1.setStart(start);
        bookingDto1.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto1);
        NotFoundException validationExceptionIncorrectUserId = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userDtoCreate1.getId() + 5, bookingDtoCreate1.getId()));
        assertThat(validationExceptionIncorrectUserId.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate1.getId() + 5) + " не найден"));
    }

    @Test
    void getBookingByIdIncorrectIdUser() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        UserDto userDto3 = new UserDto();
        userDto3.setName("test3");
        userDto3.setEmail("test3@test3.ru");
        UserDto userDtoCreate3 = userService.createUser(userDto3);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(itemDtoCreate2.getId());
        bookingDto1.setStart(start);
        bookingDto1.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto1);
        NotFoundException validationExceptionIncorrectUserId = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userDtoCreate3.getId(), bookingDtoCreate1.getId()));
        assertThat(validationExceptionIncorrectUserId.getMessage(),
                equalTo("Бронирование не найдено"));
    }

    @Test
    void getBookingByIdIncorrectIdBooking() throws Exception {
        //Добавление пользователей в бд
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        //Добавление предметов в бд
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        //Создание бронирования
        end = LocalDateTime.now().plusSeconds(3);
        start = LocalDateTime.now().plusSeconds(2);
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(itemDtoCreate2.getId());
        bookingDto1.setStart(start);
        bookingDto1.setEnd(end);
        BookingDto bookingDtoCreate1 = bookingService.createBooking(userDtoCreate1.getId(), bookingDto1);
        NotFoundException validationExceptionIncorrectBookingId = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userDtoCreate1.getId(), bookingDtoCreate1.getId() + 5));
        assertThat(validationExceptionIncorrectBookingId.getMessage(),
                equalTo("Бронирование не найдено"));
    }
}

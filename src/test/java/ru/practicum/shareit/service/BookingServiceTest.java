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
    }
}

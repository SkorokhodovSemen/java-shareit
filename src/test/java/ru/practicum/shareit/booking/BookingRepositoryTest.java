package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    Item item = new Item();
    User user = new User();
    User user2 = new User();
    Booking booking = new Booking();

    @BeforeEach
    void setUp() {
        user.setEmail("test@test.ru");
        user.setName("test");
        user2.setEmail("test2@test2.ru");
        user2.setName("test2");
        item.setAvailable(true);
        item.setDescription("test");
        item.setName("test");
        item.setOwner(user);
        item.setRequestId(0);
        booking.setStatus(Status.APPROVED);
        booking.setItem(item);
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user2);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
    }

    @Test
    void findAllByUser() throws Exception {
        List<Booking> findAllByUser = bookingRepository
                .findAllByUser(2, PageRequest.of(0, 1)).getContent();
        Assertions.assertEquals(findAllByUser.size(), 1);
    }

    @Test
    void findFutureBooking() throws Exception {
        List<Booking> findFutureBooking = bookingRepository
                .findFutureBooking(2, PageRequest.of(0, 1)).getContent();
        Assertions.assertEquals(findFutureBooking.size(), 1);
    }

    @Test
    void findAllByOwner() throws Exception {
        List<Booking> findAllByOwner = bookingRepository
                .findAllByOwner(1, PageRequest.of(0, 1)).getContent();
        Assertions.assertEquals(findAllByOwner.size(), 1);
    }

    @Test
    void findBookingByItemToFree() throws Exception {
        List<Booking> findBookingByItemToFree = bookingRepository
                .findBookingByItemToFree(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Assertions.assertEquals(findBookingByItemToFree.size(), 1);
    }

    @Test
    void findBookingByItemAndOwner() throws Exception {
        List<Booking> findBookingByItemAndOwner = bookingRepository
                .findBookingByItemAndOwner(1, 1);
        Assertions.assertEquals(findBookingByItemAndOwner.size(), 1);
    }

    @Test
    void findPastBooking() throws Exception {
        Booking booking1 = new Booking();
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item);
        booking1.setEnd(LocalDateTime.now().plusSeconds(2));
        booking1.setStart(LocalDateTime.now().plusSeconds(1));
        booking1.setBooker(user2);
        TimeUnit.SECONDS.sleep(4);
        em.persist(booking1);
        List<Booking> findPastBooking = bookingRepository
                .findPastBooking(user2.getId(), PageRequest.of(0, 1)).getContent();
        Assertions.assertEquals(findPastBooking.size(), 1);
    }

    @Test
    void findWaitingBooking() throws Exception {
        Booking booking1 = new Booking();
        booking1.setStatus(Status.WAITING);
        booking1.setItem(item);
        booking1.setEnd(LocalDateTime.now().plusSeconds(20));
        booking1.setStart(LocalDateTime.now().plusSeconds(10));
        booking1.setBooker(user2);
        em.persist(booking1);
        List<Booking> findPastBooking = bookingRepository
                .findWaitingBooking(user2.getId(), PageRequest.of(0, 1)).getContent();
        Assertions.assertEquals(findPastBooking.size(), 1);
    }
}

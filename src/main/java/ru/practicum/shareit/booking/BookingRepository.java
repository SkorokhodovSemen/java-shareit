package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b FROM Booking b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    Page<Booking> findAllByUser(long userId, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(CURRENT_TIME BETWEEN b.start AND b.end)) ORDER BY b.start DESC")
    Page<Booking> findCurrentBooking(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(CURRENT_TIME > b.end)) ORDER BY b.start DESC")
    Page<Booking> findPastBooking(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(CURRENT_TIME < b.start)) ORDER BY b.start DESC")
    Page<Booking> findFutureBooking(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(b.status = 'WAITING')) ORDER BY b.start DESC")
    Page<Booking> findWaitingBooking(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(b.status = 'REJECTED')) ORDER BY b.start DESC")
    Page<Booking> findRejectedBooking(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    Page<Booking> findAllByOwner(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(CURRENT_TIME BETWEEN b.start AND b.end)) ORDER BY b.start DESC")
    Page<Booking> findCurrentBookingByOwner(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(CURRENT_TIME > b.end)) ORDER BY b.start DESC")
    Page<Booking> findPastBookingByOwner(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(CURRENT_TIME < b.start)) ORDER BY b.start DESC")
    Page<Booking> findFutureBookingByOwner(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(b.status = 'WAITING')) ORDER BY b.start DESC")
    Page<Booking> findWaitingBookingByOwner(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(b.status = 'REJECTED')) ORDER BY b.start DESC")
    Page<Booking> findRejectedBookingByOwner(long idUser, Pageable page);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.id = ?1) AND " +
            "((b.start > ?2 AND b.start < ?3) OR (b.end > ?2 AND b.end < ?3))) ORDER BY b.start DESC")
    List<Booking> findBookingByItemToFree(long itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.item.owner.id = ?2 " +
            "AND b.status = 'APPROVED' ORDER BY b.start DESC")
    List<Booking> findBookingByItemAndOwner(long itemId, long ownerId);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.booker.id = ?2 " +
            "AND b.status = 'APPROVED' AND b.end < CURRENT_TIME ORDER BY b.start DESC")
    List<Booking> findBookingByItem(long itemId, long bookerId);
}

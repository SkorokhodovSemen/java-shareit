package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b FROM Booking b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByUser(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(CURRENT_TIME BETWEEN b.start AND b.end)) ORDER BY b.start DESC")
    List<Booking> findCurrentBooking(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(CURRENT_TIME > b.end)) ORDER BY b.start DESC")
    List<Booking> findPastBooking(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(CURRENT_TIME < b.start)) ORDER BY b.start DESC")
    List<Booking> findFutureBooking(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(b.status = 'WAITING')) ORDER BY b.start DESC")
    List<Booking> findWaitingBooking(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.booker.id = ?1) AND " +
            "(b.status = 'REJECTED')) ORDER BY b.start DESC")
    List<Booking> findRejectedBooking(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByOwner(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(CURRENT_TIME BETWEEN b.start AND b.end)) ORDER BY b.start DESC")
    List<Booking> findCurrentBookingByOwner(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(CURRENT_TIME > b.end)) ORDER BY b.start DESC")
    List<Booking> findPastBookingByOwner(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(CURRENT_TIME < b.start)) ORDER BY b.start DESC")
    List<Booking> findFutureBookingByOwner(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(b.status = 'WAITING')) ORDER BY b.start DESC")
    List<Booking> findWaitingBookingByOwner(long idUser);

    @Query(value = "SELECT b FROM Booking b WHERE ((b.item.owner.id = ?1) AND " +
            "(b.status = 'REJECTED')) ORDER BY b.start DESC")
    List<Booking> findRejectedBookingByOwner(long idUser);

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

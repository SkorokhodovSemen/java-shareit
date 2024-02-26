package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT i FROM Item i WHERE i.owner.id = ?1")
    Page<Item> findByOwner(long userId, PageRequest pageRequest);

    @Query(value = "SELECT i FROM Item i WHERE i.owner.id = ?1")
    List<Item> findByOwnerWithoutPage(long userId);

    @Query(value = "SELECT i FROM Item i WHERE i.requestId = ?1")
    List<Item> getItemByRequest(long idRequest);

    @Query(value = "SELECT i FROM Item i WHERE ((UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) AND i.available IS TRUE)")
    Page<Item> getItemForBooker(String text, PageRequest pageRequest);
}

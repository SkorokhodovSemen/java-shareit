package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    @Query(value = "SELECT i FROM Item i WHERE i.owner.id = ?1")
    List<Item> findByOwner(long userId);

    @Query(value = "SELECT i FROM Item i WHERE ((UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) AND i.available IS TRUE)")
    List<Item> getItemForBooker(String text);
}

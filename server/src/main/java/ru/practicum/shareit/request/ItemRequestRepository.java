package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query(value = "SELECT i FROM ItemRequest i WHERE i.requestor.id = ?1")
    List<ItemRequest> findByRequestor(long idUser);

    Page<ItemRequest> findByRequestor_IdNot(long idUser, PageRequest pageRequest);
}

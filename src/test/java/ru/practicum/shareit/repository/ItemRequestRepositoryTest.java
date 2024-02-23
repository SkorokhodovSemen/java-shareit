package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    ItemRequest itemRequest = new ItemRequest();
    User user = new User();

    @BeforeEach
    void setUp() {
        user.setEmail("test@test.ru");
        user.setName("test");
        itemRequest.setDescription("test");
        itemRequest.setDescription("test");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        em.persist(user);
        itemRequest.setRequestor(user);
        em.persist(itemRequest);
    }

    @Test
    void findByRequestor() {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestor(user.getId());
        Assertions.assertEquals(itemRequests.size(), 1);
    }

    @Test
    void findAllItemRequest() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequestor_IdNot(user.getId(), PageRequest.of(0, 1)).getContent();
        Assertions.assertEquals(itemRequests.size(), 0);
    }
}

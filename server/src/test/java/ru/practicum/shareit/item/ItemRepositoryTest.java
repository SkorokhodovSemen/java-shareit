package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    Item item = new Item();
    User user = new User();

    @BeforeEach
    void setUp() {
        user.setEmail("test@test.ru");
        user.setName("test");
        item.setAvailable(true);
        item.setDescription("test");
        item.setName("test");
        item.setOwner(user);
        item.setRequestId(0);
        em.persist(user);
        em.persist(item);
    }

    @Test
    void findByOwner() throws Exception {
        List<Item> getItemByOwner = itemRepository
                .findByOwner(1, PageRequest.of(0, 1)).getContent();
        Assertions.assertEquals(getItemByOwner.size(), 1);
    }

    @Test
    void getItemForBooker() throws Exception {
        List<Item> getItemForBooker = itemRepository
                .getItemForBooker("test", PageRequest.of(0, 1)).getContent();
        Assertions.assertEquals(getItemForBooker.size(), 1);
    }

    @Test
    void getItemByRequest() throws Exception {
        List<Item> getItemByRequest = itemRepository
                .getItemByRequest(1);
        Assertions.assertEquals(getItemByRequest.size(), 0);
    }
}

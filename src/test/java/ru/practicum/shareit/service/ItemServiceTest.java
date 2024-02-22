package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    ItemDto itemDto1 = new ItemDto();
    ItemDto itemDto2 = new ItemDto();
    CommentDto commentDto1 = new CommentDto();

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
        commentDto1.setText("test");
    }

    @Test
    void createItemAndGetItemByOwner() {
        UserDto userDto = userService.createUser(userDto1);
        itemService.createItem(userDto.getId(), itemDto1);
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto1.getName()).getSingleResult();
        assertThat(item.getName(), equalTo(itemDto1.getName()));
        assertThat(item.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(item.getOwner(), equalTo(UserMapper.toUser(userDto)));
        List<ItemDto> itemDtos = itemService.getItemByOwner(userDto.getId(), 0, 1);
        TypedQuery<Item> query1 = em.createQuery("SELECT i FROM Item i WHERE i.owner.id = :id", Item.class);
        List<Item> items = query1.setParameter("id", userDto.getId()).getResultList();
        assertThat(itemDtos.size(), equalTo(items.size()));
        assertThat(itemDtos.get(0).getId(), equalTo(items.get(0).getId()));
    }

    @Test
    void updateItem() {
        UserDto userDto = userService.createUser(userDto1);
        ItemDto itemDto = itemService.createItem(userDto.getId(), itemDto1);
        itemService.updateItem(userDto.getId(), itemDto2, itemDto.getId());
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto2.getName()).getSingleResult();
        assertThat(item.getName(), equalTo(itemDto2.getName()));
        assertThat(item.getDescription(), equalTo(itemDto2.getDescription()));
        assertThat(item.getOwner(), equalTo(UserMapper.toUser(userDto)));
    }

    @Test
    void createCommentAndCreateBookingAndApproved() throws Exception {
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
        CommentDto commentDto = itemService.createComment(userDto.getId(), commentDto1, itemDtoTest.getId());
        TypedQuery<Comment> query =
                em.createQuery("SELECT c FROM Comment c WHERE c.authorName = :authorName", Comment.class);
        Comment comment = query.setParameter("authorName", userDto.getName()).getSingleResult();
        assertThat(comment.getCreated().truncatedTo(ChronoUnit.MINUTES)
                .compareTo(commentDto.getCreated().truncatedTo(ChronoUnit.MINUTES)), equalTo(0));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getId(), equalTo(commentDto.getId()));
    }
}

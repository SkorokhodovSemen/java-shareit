package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemMapperTest {

    @Test
    void itemMapper() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@test.ru");
        user.setName("test");
        User user2 = new User();
        user2.setId(2);
        user2.setEmail("test@test2.ru");
        user2.setName("test2");
        Item item = new Item();
        item.setId(1);
        item.setName("test");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(0);
        item.setDescription("test");
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setBooker(user2);
        booking.setItem(item);
        booking.setEnd(LocalDateTime.now().plusSeconds(5));
        booking.setStart(LocalDateTime.now().plusSeconds(3));
        booking.setStatus(Status.APPROVED);
        booking.setId(1);
        bookings.add(booking);
        ItemDto itemDto = ItemMapper.toItemOwnerDto(item, bookings);
        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        List<CommentDto> commentDtos = new ArrayList<>();
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now().plusSeconds(6));
        comment.setId(1);
        comment.setAuthorName(user2.getName());
        comment.setText("test");
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        commentDtos.add(commentDto);
        ItemDto itemDto1 = ItemMapper.toItemCommentDto(itemDto, commentDtos);
        assertThat(itemDto1.getId(), equalTo(itemDto.getId()));
        assertThat(itemDto1.getName(), equalTo(itemDto.getName()));
    }
}

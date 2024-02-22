package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final ItemDto itemDto1 = new ItemDto();
    private final ItemDto itemDto2 = new ItemDto();
    private final CommentDto commentDto1 = new CommentDto();
    private final UserDto userDto1 = new UserDto();
    private final List<ItemDto> itemDtos = new ArrayList<>();
    private Item item1 = new Item();
    private User user1 = new User();


    @BeforeEach
    void setUp() {
        itemDto1.setId(1);
        itemDto1.setDescription("test Item1");
        itemDto1.setName("test Item1");
        itemDto1.setAvailable(true);
        itemDto2.setId(1);
        itemDto2.setDescription(null);
        itemDto2.setName("test Item2");
        itemDto2.setAvailable(true);
        userDto1.setEmail("test@test.ru");
        userDto1.setId(1);
        userDto1.setName("test");
        item1 = ItemMapper.toItem(itemDto1, user1);
        user1 = UserMapper.toUser(userDto1);
        commentDto1.setCreated(LocalDateTime.now().withNano(0));
        commentDto1.setText("test Comment");
        commentDto1.setId(1);
        commentDto1.setAuthorName("test Name");
        commentDto1.setItem(item1);
        itemDtos.add(itemDto1);
    }

    @Test
    void createItem() throws Exception {
        Mockito.when(itemService.createItem(Mockito.anyLong(), Mockito.any())).thenReturn(itemDto1);
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.available").value(itemDto1.isAvailable()));
    }

    @Test
    void createComment() throws Exception {
        Mockito.when(itemService.createComment(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                .thenReturn(commentDto1);
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto1.getId()))
                .andExpect(jsonPath("$.text").value(commentDto1.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto1.getAuthorName()));
    }

    @Test
    void getItemById() throws Exception {
        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDto1);
        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.available").value(itemDto1.isAvailable()));
    }

    @Test
    void getItemByOwner() throws Exception {
        Mockito.when(itemService.getItemByOwner(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemDtos);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0]description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$[0]name").value(itemDto1.getName()))
                .andExpect(jsonPath("$[0]available").value(itemDto1.isAvailable()));
    }

    @Test
    void getItemForBooker() throws Exception {
        Mockito.when(itemService
                        .getItemForBooker(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemDtos);
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0]description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$[0]name").value(itemDto1.getName()))
                .andExpect(jsonPath("$[0]available").value(itemDto1.isAvailable()));
    }

    @Test
    void updateItem() throws Exception {
        Mockito.when(itemService
                        .updateItem(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemDto1);
        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.available").value(itemDto1.isAvailable()));
    }

    @Test
    void getItemByIdWrongIdItem() throws Exception {
        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Вещь не найдена"));
        mockMvc.perform(get("/items/{itemId}", 100)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getItemByIdWrongIdUser() throws Exception {
        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemEmptyDescription() throws Exception {
        Mockito.when(itemService.createItem(Mockito.anyLong(), Mockito.any()))
                .thenThrow(new ConflictException("Поле description не может быть пустым"));
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getItemByOwnerWrongId() throws Exception {
        Mockito.when(itemService.getItemByOwner(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 100)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getItemByOwnerWrongSize() throws Exception {
        Mockito.when(itemService.getItemByOwner(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new ValidationException("Параметры не корректны"));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "-1"))
                .andExpect(status().is4xxClientError());
    }
}

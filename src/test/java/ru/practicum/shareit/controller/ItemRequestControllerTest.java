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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final ItemRequestDto itemRequestDto1 = new ItemRequestDto();
    private final ItemDto itemDto1 = new ItemDto();
    private final UserDto userDto1 = new UserDto();
    private final List<ItemDto> itemDtos = new ArrayList<>();
    private final List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
    private Item item1 = new Item();
    private User user1 = new User();

    @BeforeEach
    void setUp() {
        itemDto1.setId(1);
        itemDto1.setDescription("test Item1");
        itemDto1.setName("test Item1");
        itemDto1.setAvailable(true);
        userDto1.setEmail("test@test.ru");
        userDto1.setId(1);
        userDto1.setName("test");
        item1 = ItemMapper.toItem(itemDto1, user1);
        user1 = UserMapper.toUser(userDto1);
        itemDtos.add(itemDto1);
        itemRequestDto1.setId(1);
        itemRequestDto1.setCreated(LocalDateTime.now());
        itemRequestDto1.setDescription("test");
        itemRequestDto1.setItems(itemDtos);
        itemRequestDto1.setRequestor(user1);
        itemRequestDtos.add(itemRequestDto1);
    }

    @Test
    void createItemRequest() throws Exception {
        Mockito.when(itemRequestService.createItemRequest(Mockito.anyLong(), Mockito.any()))
                .thenReturn(itemRequestDto1);
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto1.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto1.getDescription()));
    }

    @Test
    void findAllItemRequestForRequestor() throws Exception {
        Mockito.when(itemRequestService.findAllItemRequestForRequestor(Mockito.anyLong()))
                .thenReturn(itemRequestDtos);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(itemRequestDto1.getId()))
                .andExpect(jsonPath("$[0]description").value(itemRequestDto1.getDescription()));
    }

    @Test
    void findAllItemRequest() throws Exception {
        Mockito.when(itemRequestService.findAllItemRequest(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemRequestDtos);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(itemRequestDto1.getId()))
                .andExpect(jsonPath("$[0]description").value(itemRequestDto1.getDescription()));
    }

    @Test
    void findItemRequestById() throws Exception {
        Mockito.when(itemRequestService.findItemRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDto1);
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto1.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto1.getDescription()));
    }
    @Test
    void findItemRequestByIdWrongIdUser() throws Exception {
        Mockito.when(itemRequestService.findItemRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().is4xxClientError());
    }
    @Test
    void findItemRequestByIdWrongIdRequest() throws Exception {
        Mockito.when(itemRequestService.findItemRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Запрос не найден"));
        mockMvc.perform(get("/requests/{requestId}", 100)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }
}

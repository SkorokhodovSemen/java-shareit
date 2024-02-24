package ru.practicum.shareit.controller;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    private final EntityManager em;
    private final UserController userController;
    private final ItemRequestController itemRequestController;

    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    ItemRequestDto itemRequestDto1 = new ItemRequestDto();
    ItemRequestDto itemRequestDto2 = new ItemRequestDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("test");
        userDto1.setEmail("test@test.ru");
        userDto2.setName("test2");
        itemRequestDto1.setDescription("test");
    }

    @Test
    void createItemRequest() throws Exception {
        UserDto userDtoCreate = userController.createUser(userDto1);
        ItemRequestDto itemRequestDtoCreate =
                itemRequestController.createItemRequest(userDtoCreate.getId(), itemRequestDto1);
        assertThat(itemRequestDtoCreate.getId(), equalTo(1L));
        assertThat(UserMapper.toUserDto(itemRequestDtoCreate.getRequestor()), equalTo(userDtoCreate));
    }
}

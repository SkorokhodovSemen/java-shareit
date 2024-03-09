package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    ItemDto itemDto1 = new ItemDto();
    ItemDto itemDto2 = new ItemDto();
    ItemRequestDto itemRequestDto1 = new ItemRequestDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("test");
        userDto1.setEmail("test@test.ru");
        userDto2.setName("update");
        userDto2.setEmail("update@update.ru");
        itemDto1.setAvailable(true);
        itemDto1.setDescription("test");
        itemDto1.setName("test");
        itemDto1.setRequestId(1);
        itemDto2.setAvailable(true);
        itemDto2.setDescription("update");
        itemDto2.setName("update");
        itemRequestDto1.setDescription("test");
    }

    @Test
    void createItemRequest() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        ItemRequestDto itemRequestCreate = itemRequestService.createItemRequest(userDtoCreate.getId(), itemRequestDto1);
        TypedQuery<ItemRequest> createItemRequest =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        ItemRequest item = createItemRequest.setParameter("id", itemRequestCreate.getId()).getSingleResult();
        assertThat(item.getId(), equalTo(itemRequestCreate.getId()));
        assertThat(item.getDescription(), equalTo(itemRequestCreate.getDescription()));
        assertThat(item.getRequestor().getId(), equalTo(itemRequestCreate.getRequestor().getId()));
    }

    @Test
    void createItemRequestWrongIdUser() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(userDtoCreate.getId() + 1, itemRequestDto1));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate.getId() + 1) + " не найден"));
    }

    @Test
    void findItemRequestById() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        ItemRequestDto itemRequestCreate = itemRequestService.createItemRequest(userDtoCreate.getId(), itemRequestDto1);
        TypedQuery<ItemRequest> createItemRequest =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        ItemRequest item = createItemRequest.setParameter("id", itemRequestCreate.getId()).getSingleResult();
        ItemRequestDto itemRequestDto =
                itemRequestService.findItemRequestById(userDtoCreate.getId(), itemRequestCreate.getId());
        assertThat(item.getId(), equalTo(itemRequestDto.getId()));
        assertThat(item.getRequestor(), equalTo(itemRequestDto.getRequestor()));
    }

    @Test
    void findItemRequestByIdWrongIdUser() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        ItemRequestDto itemRequestCreate = itemRequestService.createItemRequest(userDtoCreate.getId(), itemRequestDto1);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService
                        .findItemRequestById(userDtoCreate.getId() + 1, itemRequestCreate.getId()));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate.getId() + 1) + " не найден"));
    }

    @Test
    void findItemRequestByIdWrongIdItemRequest() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        ItemRequestDto itemRequestCreate = itemRequestService.createItemRequest(userDtoCreate.getId(), itemRequestDto1);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService
                        .findItemRequestById(userDtoCreate.getId(), itemRequestCreate.getId() + 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Запроса с id = " + (itemRequestCreate.getId() + 1) + " не существует"));
    }

    @Test
    void findAllItemRequest() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemRequestDto itemRequestCreate =
                itemRequestService.createItemRequest(userDtoCreate1.getId(), itemRequestDto1);
        TypedQuery<ItemRequest> createItemRequest =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        List<ItemRequest> items = createItemRequest.setParameter("id", itemRequestCreate.getId()).getResultList();
        List<ItemRequestDto> itemRequestDtos =
                itemRequestService.findAllItemRequest(userDtoCreate2.getId(), 0, 1);
        assertThat(items.size(), equalTo(itemRequestDtos.size()));
        assertThat(items.get(0).getId(), equalTo(itemRequestDtos.get(0).getId()));
    }

    @Test
    void findAllItemRequestWrongIdUser() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        ItemRequestDto itemRequestCreate =
                itemRequestService.createItemRequest(userDtoCreate1.getId(), itemRequestDto1);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService
                        .findAllItemRequest(userDtoCreate1.getId() + 1, 0, 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate1.getId() + 1) + " не найден"));
    }

//    @Test
//    void findAllItemRequestWrongParameters() {
//        UserDto userDtoCreate1 = userService.createUser(userDto1);
//        ItemRequestDto itemRequestCreate =
//                itemRequestService.createItemRequest(userDtoCreate1.getId(), itemRequestDto1);
//        ValidationException validationException = assertThrows(ValidationException.class,
//                () -> itemRequestService
//                        .findAllItemRequest(userDtoCreate1.getId(), -1, 1));
//        assertThat(validationException.getMessage(),
//                equalTo("Проверьте правильность введенных параметров"));
//    }

    @Test
    void findAllItemRequestWithItem() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemRequestDto itemRequestCreate =
                itemRequestService.createItemRequest(userDtoCreate1.getId(), itemRequestDto1);
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate2.getId(), itemDto1);
        TypedQuery<ItemRequest> createItemRequest =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        List<ItemRequest> items = createItemRequest.setParameter("id", itemRequestCreate.getId()).getResultList();
        List<ItemRequestDto> itemRequestDtos =
                itemRequestService.findAllItemRequest(userDtoCreate2.getId(), 0, 1);
        assertThat(items.size(), equalTo(itemRequestDtos.size()));
        assertThat(items.get(0).getId(), equalTo(itemRequestDtos.get(0).getId()));
        assertThat(itemRequestDtos.get(0).getItems().size(), equalTo(1));
    }

    @Test
    void findAllItemRequestForRequestorWithItem() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemRequestDto itemRequestCreate =
                itemRequestService.createItemRequest(userDtoCreate1.getId(), itemRequestDto1);
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate2.getId(), itemDto1);
        TypedQuery<ItemRequest> createItemRequest =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        List<ItemRequest> items = createItemRequest.setParameter("id", itemRequestCreate.getId()).getResultList();
        List<ItemRequestDto> itemRequestDtos =
                itemRequestService.findAllItemRequestForRequestor(userDtoCreate1.getId());
        assertThat(items.size(), equalTo(itemRequestDtos.size()));
        assertThat(items.get(0).getId(), equalTo(itemRequestDtos.get(0).getId()));
        assertThat(itemRequestDtos.get(0).getItems().size(), equalTo(1));
    }

    @Test
    void findAllItemRequestForRequestorWithItemWrongIdUser() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemRequestDto itemRequestCreate =
                itemRequestService.createItemRequest(userDtoCreate1.getId(), itemRequestDto1);
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate2.getId(), itemDto1);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.findAllItemRequestForRequestor(userDtoCreate1.getId() + 2));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate1.getId() + 2) + " не найден"));
    }
}

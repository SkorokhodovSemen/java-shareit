package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

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
        itemDto2.setAvailable(true);
        itemDto2.setDescription("update");
        itemDto2.setName("update");
        itemRequestDto1.setDescription("test");
    }

    @Test
    void testAllMethodInItemRequestService() {
        UserDto userDto = userService.createUser(userDto1);
        UserDto userDtoTest = userService.createUser(userDto2);
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(userDto.getId(), itemRequestDto1);
        itemDto2.setRequestId(itemRequestDto.getId());
        itemService.createItem(userDtoTest.getId(), itemDto2);
        TypedQuery<ItemRequest> createItemRequest =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        ItemRequest item = createItemRequest.setParameter("id", itemRequestDto.getId()).getSingleResult();
        assertThat(item.getId(), equalTo(itemRequestDto.getId()));
        assertThat(item.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(item.getRequestor().getId(), equalTo(itemRequestDto.getRequestor().getId()));
        List<ItemRequestDto> itemRequestDtos =
                itemRequestService.findAllItemRequest(userDtoTest.getId(), 0, 1);
        TypedQuery<ItemRequest> findAllItemRequest =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.requestor.id = :id", ItemRequest.class);
        List<ItemRequest> itemRequests = findAllItemRequest.setParameter("id", userDto.getId()).getResultList();
        assertThat(itemRequests.size(), equalTo(itemRequestDtos.size()));
        assertThat(itemRequests.get(0).getId(), equalTo(itemRequestDtos.get(0).getId()));
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequestDtos.get(0).getDescription()));
        ItemRequestDto itemRequestDto2 =
                itemRequestService.findItemRequestById(userDto.getId(), itemRequestDto.getId());
        TypedQuery<ItemRequest> findItemRequestById =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.requestor.id = :id", ItemRequest.class);
        ItemRequest itemRequest = findItemRequestById.setParameter("id", itemRequestDto2.getId()).getSingleResult();
        assertThat(itemRequest.getId(), equalTo(itemRequestDto2.getId()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(itemRequestDto2.getRequestor().getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto2.getDescription()));
        List<ItemRequestDto> itemRequestDtos1 =
                itemRequestService.findAllItemRequestForRequestor(userDto.getId());
        TypedQuery<ItemRequest> findAllItemRequestForRequestor =
                em.createQuery("SELECT i FROM ItemRequest i WHERE i.requestor.id <> :id", ItemRequest.class);
        List<ItemRequest> itemRequests1 =
                findAllItemRequestForRequestor.setParameter("id", userDtoTest.getId()).getResultList();
        assertThat(itemRequests1.size(), equalTo(itemRequestDtos1.size()));
        assertThat(itemRequests1.get(0).getDescription(), equalTo(itemRequestDtos1.get(0).getDescription()));
        assertThat(itemRequests1.get(0).getRequestor().getId(),
                equalTo(itemRequestDtos1.get(0).getRequestor().getId()));

    }
}

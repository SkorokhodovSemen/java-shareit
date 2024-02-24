package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceTestMock {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void findItemRequestByIdWrongIdItemRequest() throws Exception {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setName("test");
        user.setId(1);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Запроса с id = 1 не существует"));
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.findItemRequestById(1, 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Запроса с id = 1 не существует"));
    }

    @Test
    void findItemRequestByIdWrongIdUser() throws Exception {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setName("test");
        user.setId(2);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id = 1 не найден"));
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.findItemRequestById(1, 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = 1 не найден"));
    }

    @Test
    void findAllItemRequestWithWrongIdUser() throws Exception {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setName("test");
        user.setId(2);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByRequestor_IdNot(Mockito.anyLong(), Mockito.any()))
                .thenThrow(new NotFoundException("Пользователь с id = 1 не найден"));
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.findAllItemRequest(1, 1,1));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = 1 не найден"));
    }
}

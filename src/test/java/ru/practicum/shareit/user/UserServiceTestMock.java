package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTestMock {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void findUserById() throws Exception {
        User user = new User();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        UserDto userDto = userService.findUserById(1);
        assertThat(userDto.getName(), equalTo(user.getName()));
    }

    @Test
    void findUserByIdWrongId() throws Exception {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.findUserById(1));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь не найден"));
    }
}

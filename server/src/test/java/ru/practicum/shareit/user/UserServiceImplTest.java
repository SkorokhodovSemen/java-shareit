package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final UserService userService;
    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    UserDto userDto3 = new UserDto();
    UserDto userDto4 = new UserDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("test");
        userDto1.setEmail("test@test.ru");
        userDto2.setEmail("update@update.ru");
        userDto3.setName("test3");
        userDto4.setName("test4");
        userDto4.setEmail("test4@test4.ru");
    }

    @Test
    void createUserAndGetAllUser() {
        userService.createUser(userDto1);
        UserDto userDto = userService.findUserById(1);
        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(userDto1.getName()));
        assertThat(userDto.getEmail(), equalTo(userDto1.getEmail()));
        List<UserDto> userDtos = userService.getAllUser();
        List<UserDto> userDtos1 = new ArrayList<>();
        userDtos1.add(userDto);
        assertThat(userDtos1.size(), equalTo(userDtos.size()));
        assertThat(userDtos1.get(0).getId(), equalTo(userDtos.get(0).getId()));
    }

    @Test
    void updateUserAndDeleteUser() {
        UserDto userDto = userService.createUser(userDto1);
        userService.updateUser(1, userDto2);
        UserDto userDto1 = userService.findUserById(1);
        assertThat(userDto1.getId(), notNullValue());
        assertThat(userDto1.getEmail(), equalTo(userDto2.getEmail()));
        UserDto userDto5 = userService.updateUser(userDto.getId(), userDto3);
        assertThat(userDto5.getName(), equalTo(userDto3.getName()));
        UserDto userDto6 = userService.updateUser(userDto.getId(), userDto4);
        assertThat(userDto6.getName(), equalTo(userDto4.getName()));
        userService.deleteUserById(userDto.getId());
        assertThat(userService.getAllUser().size(), equalTo(0));
    }

    @Test
    void findUserById() {
        UserDto userDto = userService.createUser(userDto1);
        UserDto userDto1 = userService.findUserById(1);
        assertThat(userDto1.getEmail(), equalTo(userDto.getEmail()));
        assertThat(userDto1.getId(), equalTo(userDto.getId()));
        try {
            userService.findUserById(100);
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), equalTo("Пользователь не найден"));
        }
    }
}

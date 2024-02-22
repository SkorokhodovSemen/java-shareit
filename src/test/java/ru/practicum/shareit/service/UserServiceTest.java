package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {


    private final EntityManager em;
    private final UserService userService;
    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("test");
        userDto1.setEmail("test@test.ru");
        userDto2.setEmail("update@update.ru");
    }

    @Test
    void createUserAndGetAllUser() {
        userService.createUser(userDto1);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail()).getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
        List<UserDto> userDtos = userService.getAllUser();
        TypedQuery<User> query1 = em.createQuery("SELECT u FROM User u", User.class);
        List<User> users = query1.getResultList();
        assertThat(users.size(), equalTo(userDtos.size()));
        assertThat(users.get(0).getId(), equalTo(userDtos.get(0).getId()));
    }

    @Test
    void updateUserAndDeleteUser() {
        UserDto userDto = userService.createUser(userDto1);
        userService.updateUser(1, userDto2);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDto2.getEmail()));
        userService.deleteUserById(userDto.getId());
        assertThat(userService.getAllUser().size(), equalTo(0));
    }

    @Test
    void findUserById() {
        userService.createUser(userDto1);
        UserDto userDto = userService.findUserById(1);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail()).getSingleResult();
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getId(), equalTo(userDto.getId()));
    }
}

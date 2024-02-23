package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    User user = new User();

    @BeforeEach
    void setUp() {
        user.setEmail("test@test.ru");
        user.setName("test");
        em.persist(user);
    }

    @Test
    void findById() throws Exception {
        Optional<User> getUserById = userRepository.findById(1L);
        Assertions.assertEquals(getUserById.isPresent(), true);
    }

    @Test
    void save() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("dto@dto.ru");
        userDto.setName("dto");
        userRepository.save(UserMapper.toUser(userDto));
        Optional<User> getUserById = userRepository.findById(2L);
        Assertions.assertEquals(getUserById.isPresent(), true);
    }

    @Test
    void findAll() throws Exception {
        List<User> getUserAll = userRepository.findAll();
        Assertions.assertEquals(getUserAll.size(), 1);
    }
}

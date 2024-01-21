package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userRepository = new HashMap<>();
    private final Map<String, User> userEmailRepository = new HashMap<>();
    private long id = 1;

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(userRepository.values());
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.get(userId);
    }

    @Override
    public User createUser(User user) {
        user.setId(id);
        id++;
        userRepository.put(user.getId(), user);
        userEmailRepository.put(user.getEmail(), user);
        log.info("Пользователь с id = {} успешно создан", id - 1);
        return user;
    }

    @Override
    public User updateUser(long userId, User user) {
        userEmailRepository.remove(userRepository.get(userId).getEmail());
        userRepository.get(userId).setEmail(user.getEmail());
        userRepository.get(userId).setName(user.getName());
        userEmailRepository.put(userRepository.get(userId).getEmail(), userRepository.get(userId));
        log.info("Пользователь с id = {} обновлен", userId);
        return userRepository.get(userId);
    }

    @Override
    public User updateUserFieldName(long userId, User user) {
        userRepository.get(userId).setName(user.getName());
        userEmailRepository.put(userRepository.get(userId).getEmail(), userRepository.get(userId));
        log.info("Поле name у пользователя с id = {} обновлено", userId);
        return userRepository.get(userId);
    }

    @Override
    public User updateUserFieldEmail(long userId, User user) {
        userEmailRepository.remove(userRepository.get(userId).getEmail());
        userRepository.get(userId).setEmail(user.getEmail());
        userEmailRepository.put(userRepository.get(userId).getEmail(), userRepository.get(userId));
        log.info("Поле email у пользователя с id = {} обновлено", userId);
        return userRepository.get(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        userEmailRepository.remove(userRepository.get(userId).getEmail());
        userRepository.remove(userId);
        log.info("Пользователь с id = {} удален", userId);
    }

    public Map<Long, User> getUserRepository() {
        return userRepository;
    }

    public Map<String, User> getUserEmailRepository() {
        return userEmailRepository;
    }
}

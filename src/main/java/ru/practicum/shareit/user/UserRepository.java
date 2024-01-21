package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> getAllUser();

    User findUserById(long userId);

    User createUser(User user);

    User updateUser(long userId, User user);

    User updateUserFieldName(long userId, User user);

    User updateUserFieldEmail(long userId, User user);

    void deleteUserById(long userId);
}

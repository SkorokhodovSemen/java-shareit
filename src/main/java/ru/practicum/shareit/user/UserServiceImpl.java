package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepository;

    @Override
    public List<UserDto> getAllUser() {
        return userRepository.getAllUser()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(long userId) {
        validateFoundForUser(userId);
        return UserMapper.toUserDto(userRepository.findUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validateForUser(userDto);
        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        validateFoundForUser(userId);
        if (userDto.getEmail() != null && userDto.getName() != null) {
            return UserMapper.toUserDto(userRepository.updateUser(userId, UserMapper.toUser(userDto)));
        }
        if (userDto.getEmail() != null) {
            validateForExistEmail(userId, userDto);
            return UserMapper.toUserDto(userRepository.updateUserFieldEmail(userId, UserMapper.toUser(userDto)));
        }
        return UserMapper.toUserDto(userRepository.updateUserFieldName(userId, UserMapper.toUser(userDto)));
    }

    @Override
    public void deleteUserById(long userId) {
        validateFoundForUser(userId);
        userRepository.deleteUserById(userId);
    }

    private void validateForUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.info("Пользователь не заполнил поле email");
            throw new ValidationException("Пользователь не заполнил поле email");
        }
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("Пользователь неверно ввел email = {}", userDto.getEmail());
            throw new ValidationException("Неверный формат email");
        }
        if (userRepository.getUserEmailRepository().containsKey(userDto.getEmail())) {
            log.info("Пользователь с таким email = {} уже существует", userDto.getEmail());
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        if (userDto.getName().isBlank() || userDto.getName() == null) {
            log.info("Пользователь не заполнил поле name = {}", userDto.getName());
            throw new ValidationException("Пользователь не заполнил поле name");
        }
    }

    private void validateForExistEmail(long userId, UserDto userDto) {
        if (userRepository.getUserEmailRepository().containsKey(userDto.getEmail())) {
            if (userRepository.getUserEmailRepository().get(userDto.getEmail()).getId() != userId)
                throw new ConflictException("Пользователь с таким email уже существует");
        }
    }

    private void validateFoundForUser(long userId) {
        if (!userRepository.getUserRepository().containsKey(userId)) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }
}

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(long userId) {
        Optional<User> user = userRepository.findById(userId);
        validateFoundForUser(user, userId);
        return UserMapper.toUserDto(user.get());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        validateFoundForUser(userOptional, userId);
        if (userDto.getEmail() != null && userDto.getName() != null) {
            User user = UserMapper.toUser(userDto);
            user.setId(userId);
            return UserMapper.toUserDto(userRepository.save(user));
        }
        if (userDto.getEmail() != null) {
            User user = userOptional.get();
            user.setEmail(userDto.getEmail());
            return UserMapper.toUserDto(userRepository.save(user));
        }
        User user = userOptional.get();
        user.setName(userDto.getName());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        Optional<User> user = userRepository.findById(userId);
        validateFoundForUser(user, userId);
        userRepository.deleteById(userId);
    }

    private void validateFoundForUser(Optional<User> user, long userId) {
        if (user.isEmpty()) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }
}

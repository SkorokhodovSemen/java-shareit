package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto getItemById(long itemId, long idUser) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        validateFoundForItem(itemId, itemOptional);
        Item item = itemOptional.get();
        List<Comment> comments = commentRepository.findByItem(itemId);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(CommentMapper.toCommentDto(comment));
        }
        if (item.getOwner().getId() != idUser) {
            return ItemMapper.toItemCommentDto(ItemMapper.toItemDto(item), commentDtos);
        }
        List<Booking> bookings = bookingRepository.findBookingByItemAndOwner(itemId, idUser);
        if (bookings.isEmpty()) {
            return ItemMapper.toItemCommentDto(ItemMapper.toItemDto(item), commentDtos);
        } else {
            return ItemMapper.toItemCommentDto(ItemMapper.toItemOwnerDto(item, bookings), commentDtos);
        }
    }

    @Override
    public List<ItemDto> getItemByOwner(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        List<Item> items = itemRepository.findByOwner(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findBookingByItemAndOwner(item.getId(), item.getOwner().getId());
            if (bookings.isEmpty()) {
                itemDtos.add(ItemMapper.toItemDto(item));
            } else {
                itemDtos.add(ItemMapper.toItemOwnerDto(item, bookings));
            }
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> getItemForBooker(String text, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.getItemForBooker(text.toLowerCase().trim())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        validateFoundForUser(userOptional, userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, userOptional.get())));
    }

    @Override
    @Transactional
    public CommentDto createComment(long idUser, CommentDto commentDto, long idItem) {
        Optional<User> userOptional = userRepository.findById(idUser);
        validateFoundForUser(userOptional, idUser);
        Optional<Item> itemOptional = itemRepository.findById(idItem);
        validateFoundForItem(idItem, itemOptional);
        List<Booking> bookings = bookingRepository.findBookingByItem(idItem, idUser);
        if (bookings.isEmpty()) {
            log.info("Пользователь с id = {} не бронировал эту вещь", idUser);
            throw new ValidationException("Пользователь не бронировал эту вещь");
        }
        return CommentMapper.toCommentDto(commentRepository
                .save(CommentMapper.toComment(commentDto, itemOptional.get(), userOptional.get())));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        Optional<User> userOptional = userRepository.findById(userId);
        validateFoundForUser(userOptional, userId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        validateFoundForItem(itemId, itemOptional);
        validateForOwner(userId, itemOptional.get().getOwner().getId(), itemId);
        if (itemDto.getDescription() != null && itemDto.getName() != null) {
            Item item = ItemMapper.toItem(itemDto, userOptional.get());
            item.setId(itemId);
            return ItemMapper.toItemDto(itemRepository.save(item));
        }
        if (itemDto.getDescription() != null) {
            Item item = itemOptional.get();
            item.setDescription(itemDto.getDescription());
            return ItemMapper
                    .toItemDto(itemRepository.save(item));
        }
        if (itemDto.getName() != null) {
            Item item = itemOptional.get();
            item.setName(itemDto.getName());
            return ItemMapper
                    .toItemDto(itemRepository.save(item));
        }
        Item item = itemOptional.get();
        item.setAvailable(itemDto.isAvailable());
        return ItemMapper
                .toItemDto(itemRepository.save(item));
    }

    private void validateFoundForItem(long itemId, Optional<Item> itemOptional) {
        if (itemOptional.isEmpty()) {
            log.info("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь с данным id не найдена");
        }
    }

    private void validateFoundForUser(Optional<User> user, long userId) {
        if (user.isEmpty()) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateForOwner(long userId, long ownerId, long itemId) {
        if (ownerId != userId) {
            throw new NotFoundException("У пользователя с id = " + userId + " нет вещи с id = " + itemId);
        }
    }
}

package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public abstract class CommentMapper {
    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthorName(user.getName());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(comment.getItem());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}

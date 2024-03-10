package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.valid.Create;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class CommentDto {
    @NotBlank(groups = Create.class)
    private String text;
}

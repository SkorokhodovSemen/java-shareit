package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.valid.Create;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotBlank(message = "Пользователь не заполнил поле description", groups = Create.class)
    private String description;
}

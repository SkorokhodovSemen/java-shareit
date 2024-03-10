package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.valid.Create;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(message = "Пользователь не заполнил поле name", groups = Create.class)
    private String name;
    @NotBlank(message = "Пользователь не заполнил поле description", groups = Create.class)
    private String description;
    @AssertTrue(message = "Поле не может быть false", groups = Create.class)
    private boolean available;
    private long requestId;
}

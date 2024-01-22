package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor
public class ItemDto {
    private long id;
    @NotNull(message = "Пользователь не заполнил поле name")
    @NotBlank(message = "Пользователь не заполнил поле name")
    private String name;
    @NotNull(message = "Пользователь не заполнил поле description")
    @NotBlank(message = "Пользователь не заполнил поле description")
    private String description;
    @AssertTrue(message = "Пользователь не заполнил поле available")
    private boolean available;
}

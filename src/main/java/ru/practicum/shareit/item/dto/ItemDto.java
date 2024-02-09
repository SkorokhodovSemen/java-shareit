package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(message = "Пользователь не заполнил поле name")
    private String name;
    @NotBlank(message = "Пользователь не заполнил поле description")
    private String description;
    @AssertTrue(message = "Пользователь не заполнил поле available")
    private boolean available;
    private BookingItem lastBooking;
    private BookingItem nextBooking;
}
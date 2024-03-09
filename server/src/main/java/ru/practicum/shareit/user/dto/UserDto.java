package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(message = "Поле name не заполнено")
    private String name;
    @NotBlank(message = "Поле email не заполнено")
    @Email(message = "Поле email заполнено неверно")
    private String email;
}

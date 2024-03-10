package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.valid.Create;
import ru.practicum.shareit.valid.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(message = "Поле name не заполнено", groups = Create.class)
    private String name;
    @NotBlank(message = "Поле email не заполнено", groups = Create.class)
    @Email(message = "Поле email заполнено неверно", groups = {Create.class, Update.class})
    private String email;
}
package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.valid.Create;
import ru.practicum.shareit.valid.CheckStartBeforeEnd;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@CheckStartBeforeEnd(groups = Create.class)
public class BookingDto {
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;
    @NotNull(groups = Create.class)
    private LocalDateTime end;
    private long itemId;
}

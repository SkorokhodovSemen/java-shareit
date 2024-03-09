package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class BookingDto {
    @NotNull(groups = Create.class)
    private LocalDateTime start;
    @NotNull(groups = Create.class)
    private LocalDateTime end;
    private long itemId;
}

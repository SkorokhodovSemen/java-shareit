package ru.practicum.shareit.valid;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<CheckStartBeforeEnd, BookingDto> {

    @Override
    public void initialize(CheckStartBeforeEnd checkStartBeforeEnd) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        return start != null && end != null && !start.isEqual(end) && !end.isBefore(start);
    }
}

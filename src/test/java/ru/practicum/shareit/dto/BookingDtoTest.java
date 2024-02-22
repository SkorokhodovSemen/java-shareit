package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testItemDto() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(5).truncatedTo(ChronoUnit.SECONDS));
        bookingDto.setStart(LocalDateTime.now().plusSeconds(4).truncatedTo(ChronoUnit.SECONDS));
        bookingDto.setItemId(1);
        bookingDto.setBooker(new User());
        bookingDto.setId(1);
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}

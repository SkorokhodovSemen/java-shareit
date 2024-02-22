package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final BookingDto bookingDto = new BookingDto();
    private final List<BookingDto> bookingDtos = new ArrayList<>();

    @BeforeEach
    void setUp() {
        bookingDto.setStatus(Status.APPROVED);
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setId(1);
        bookingDto.setItemId(1);
        bookingDtos.add(bookingDto);
    }

    @Test
    void createBooking() throws Exception {
        Mockito.when(bookingService.createBooking(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void approvedBooking() throws Exception {
        Mockito.when(bookingService
                        .approvedBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDto);
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getBookingById() throws Exception {
        Mockito.when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getBookingForUser() throws Exception {
        Mockito.when(bookingService
                        .getBookingForUser(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bookingDtos);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0]itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$[0]status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getBookingForOwner() throws Exception {
        Mockito.when(bookingService
                        .getBookingForOwner(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bookingDtos);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0]itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$[0]status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getBookingByIdWrongId() throws Exception {
        Mockito.when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Бронирование не найдено"));
        mockMvc.perform(get("/bookings/{bookingId}", 100)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }
}

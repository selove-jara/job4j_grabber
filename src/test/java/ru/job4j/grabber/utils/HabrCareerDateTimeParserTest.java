package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HabrCareerDateTimeParserTest {
    @Test
    public void whenParseValidDateTimeReturnLocalDateTime() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String dateTimeString = "2023-10-01T12:30:00";
        LocalDateTime expected = LocalDateTime.of(2023, 10, 1, 12, 30);
        LocalDateTime result = parser.parse(dateTimeString);
        assertEquals(expected, result);
    }

    @Test
    public void whenParseInvalidDateTimeThrowException() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String invalidDateTimeString = "invalid-date-time";
        assertThrows(DateTimeParseException.class, () -> {
            parser.parse(invalidDateTimeString);
        });
    }
}
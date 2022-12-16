package de.unibi.agbi.biodwh2.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryUtilsTest {
    @Test
    void parse32BitTimestamp() {
        final LocalDateTime dt = BinaryUtils.parseMSDOSDateTime((byte) 0b00110011, (byte) 0b10111000, (byte) 0b11010010,
                                                                (byte) 0b01010010);
        assertEquals(2021, dt.getYear());
        assertEquals(6, dt.getMonthValue());
        assertEquals(18, dt.getDayOfMonth());
        assertEquals(23, dt.getHour());
        assertEquals(1, dt.getMinute());
        assertEquals(38, dt.getSecond());
    }
}
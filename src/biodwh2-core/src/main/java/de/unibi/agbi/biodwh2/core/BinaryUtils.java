package de.unibi.agbi.biodwh2.core;

import java.time.LocalDateTime;

public final class BinaryUtils {
    private BinaryUtils() {
    }

    /**
     * For zip files the modification datetime of the header is encoded in little endian as follows:
     * <ul>
     * <li>
     *     File modification time - stored in standard MS-DOS format:
     *     <ul>
     *         <li>Bits 00-04: seconds divided by 2</li>
     *         <li>Bits 05-10: minute</li>
     *         <li>Bits 11-15: hour</li>
     *     </ul>
     * </li>
     * <li>
     *     File modification date - stored in standard MS-DOS format:
     *     <ul>
     *         <li>Bits 00-04: day</li>
     *         <li>Bits 05-08: month</li>
     *         <li>Bits 09-15: years from 1980</li>
     *     </ul>
     * </li>
     * </ul>
     *
     * @param data Four bytes in little endian order with the first two bytes representing the time and the last two
     *             representing the date.
     */
    public static LocalDateTime parseMSDOSDateTime(byte... data) {
        if (data == null || data.length < 4)
            return null;
        final int time = (data[0] & 0xFF) + ((data[1] & 0xFF) << 8);
        final int seconds = (time & 0b1_1111) * 2;
        final int minutes = (time >> 5) & 0b11_1111;
        final int hours = (time >> 11) & 0b1_1111;
        final int date = (data[2] & 0xFF) + ((data[3] & 0xFF) << 8);
        final int day = date & 0b1_1111;
        final int month = (date >> 5) & 0b1111;
        final int year = 1980 + ((date >> 9) & 0b111_1111);
        return LocalDateTime.of(year, month, day, hours, minutes, seconds);
    }
}

package de.unibi.agbi.biodwh2.core.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class VersionTest {
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3, -4, -5, -6, -7, -8, -9, -10})
    public void testConstructor(int illegalNumber) {
        assertThrows(IllegalArgumentException.class, () -> new Version(illegalNumber, 0));
        assertThrows(IllegalArgumentException.class, () -> new Version(0, illegalNumber));
        assertThrows(IllegalArgumentException.class, () -> new Version(0, 0, illegalNumber));
        assertThrows(IllegalArgumentException.class, () -> new Version(0, 0, 0, illegalNumber));
    }

    @Test
    public void testCompareTo() {
        Version v = new Version(1, 2, 3, 4);
        assertEquals(0, v.compareTo(new Version(1, 2, 3, 4)));
        assertEquals(1, v.compareTo(new Version(1, 0)));
        assertEquals(-1, v.compareTo(new Version(2, 0)));
        assertEquals(1, v.compareTo(new Version(1, 1)));
        assertEquals(-1, v.compareTo(new Version(1, 3)));
        assertEquals(1, v.compareTo(new Version(1, 2, 2)));
        assertEquals(-1, v.compareTo(new Version(1, 2, 4)));
        assertEquals(1, v.compareTo(new Version(1, 2, 3, 3)));
        assertEquals(-1, v.compareTo(new Version(1, 2, 3, 5)));
    }

    @Test
    public void testEquals() {
        Version v = new Version(1, 2, 3, 4);
        assertNotEquals(null, v);
        assertNotEquals("1.2.3.4", v);
        assertNotEquals(v, new Version(1, 1, 1));
        assertEquals(v, v);
        assertEquals(v, new Version(1, 2, 3, 4));
    }

    @Test
    public void testToString() {
        Version v = new Version(1, 2);
        assertEquals("1.2", v.toString());
        v = new Version(1, 2, 3);
        assertEquals("1.2.3", v.toString());
        v = new Version(1, 2, 3, 4);
        assertEquals("1.2.3.4", v.toString());
    }

    @Test
    public void testParse() {
        assertThrows(NullPointerException.class, () -> Version.parse(null));
        assertThrows(NumberFormatException.class, () -> Version.parse("1"));
        assertThrows(NumberFormatException.class, () -> Version.parse("1.2.3.4.5"));
        assertEquals(new Version(1, 2), Version.parse("1.2"));
        assertEquals(new Version(1, 2, 3), Version.parse("1.2.3"));
        assertEquals(new Version(1, 2, 3, 4), Version.parse("1.2.3.4"));
    }

    @Test
    public void testTryParse() {
        assertNull(Version.tryParse(null));
        assertNull(Version.tryParse("1"));
        assertNull(Version.tryParse("1.2.3.4.5"));
        assertEquals(new Version(1, 2), Version.tryParse("1.2"));
        assertEquals(new Version(1, 2, 3), Version.tryParse("1.2.3"));
        assertEquals(new Version(1, 2, 3, 4), Version.tryParse("1.2.3.4"));
    }
}
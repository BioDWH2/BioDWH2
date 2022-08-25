package de.unibi.agbi.biodwh2.core.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextUtilsTest {
    @Test
    void getProgressText() {
        assertEquals("10/100 (10.00%)", TextUtils.getProgressText(10, 100));
    }
}
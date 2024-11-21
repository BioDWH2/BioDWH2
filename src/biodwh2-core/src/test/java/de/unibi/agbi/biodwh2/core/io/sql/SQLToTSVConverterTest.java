package de.unibi.agbi.biodwh2.core.io.sql;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SQLToTSVConverterTest {
    @Test
    void testEscape() throws IOException {
        final String statement = "(9865321,'Bertold','B','Schrank',NULL,NULL),(428294,NULL,'LM','\\'t Hart',NULL,NULL),(428292,NULL,'S','Heim',NULL,NULL);";
        final List<String[]> rows = new ArrayList<>();
        SQLToTSVConverter.handleInsertRows(statement, 0, rows::add);
        // First entry
        assertEquals("9865321", rows.get(0)[0]);
        assertEquals("\"Bertold\"", rows.get(0)[1]);
        assertEquals("\"B\"", rows.get(0)[2]);
        assertEquals("\"Schrank\"", rows.get(0)[3]);
        assertNull(rows.get(0)[4]);
        assertNull(rows.get(0)[5]);
        // Second entry
        assertEquals("428294", rows.get(1)[0]);
        assertNull(rows.get(1)[1]);
        assertEquals("\"LM\"", rows.get(1)[2]);
        assertEquals("\"'t Hart\"", rows.get(1)[3]);
        assertNull(rows.get(1)[4]);
        assertNull(rows.get(1)[5]);
        // Third entry
        assertEquals("428292", rows.get(2)[0]);
        assertNull(rows.get(2)[1]);
        assertEquals("\"S\"", rows.get(2)[2]);
        assertEquals("\"Heim\"", rows.get(2)[3]);
        assertNull(rows.get(2)[4]);
        assertNull(rows.get(2)[5]);
    }
}
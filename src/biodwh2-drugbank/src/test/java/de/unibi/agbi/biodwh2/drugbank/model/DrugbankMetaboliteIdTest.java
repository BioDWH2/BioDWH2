package de.unibi.agbi.biodwh2.drugbank.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DrugbankMetaboliteIdTest {
    private void assertValid(String value) {
        DrugbankMetaboliteId id = new DrugbankMetaboliteId();
        id.value = value;
        assertTrue(id.isValid(), value);
    }

    private void assertInvalid(String value) {
        DrugbankMetaboliteId id = new DrugbankMetaboliteId();
        id.value = value;
        assertFalse(id.isValid(), value);
    }

    @Test
    public void isValid() {
        assertInvalid(null);
        assertInvalid("");
        assertInvalid("invalid");
        assertValid("DBMET12345");
        assertInvalid("DBMET1234");
        assertInvalid("DBMET123456");
    }
}
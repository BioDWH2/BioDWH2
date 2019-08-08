package de.unibi.agbi.biodwh2.drugbank.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DrugbankMetaboliteIdTest {
    private void assertValid(String value) {
        DrugbankMetaboliteId id = new DrugbankMetaboliteId();
        id.value = value;
        assertTrue(value, id.isValid());
    }

    private void assertInvalid(String value) {
        DrugbankMetaboliteId id = new DrugbankMetaboliteId();
        id.value = value;
        assertFalse(value, id.isValid());
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
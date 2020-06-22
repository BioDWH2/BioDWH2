package de.unibi.agbi.biodwh2.drugbank.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DrugbankMetaboliteStructureIdTest {
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
    void isValid() {
        assertInvalid(null);
        assertInvalid("");
        assertInvalid("invalid");
        assertValid("DBMET12345");
        assertValid("DBMET54132");
        assertInvalid("DBMET1234");
        assertInvalid("DBMET123456");
    }
}
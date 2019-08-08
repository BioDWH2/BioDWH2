package de.unibi.agbi.biodwh2.drugbank.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DrugbankDrugSaltIdTest {
    private void assertValid(String value) {
        DrugbankDrugSaltId id = new DrugbankDrugSaltId();
        id.value = value;
        assertTrue(value, id.isValid());
    }

    private void assertInvalid(String value) {
        DrugbankDrugSaltId id = new DrugbankDrugSaltId();
        id.value = value;
        assertFalse(value, id.isValid());
    }

    @Test
    public void isValid() {
        assertInvalid(null);
        assertInvalid("");
        assertInvalid("invalid");
        assertValid("DB12345");
        assertInvalid("DB1234");
        assertInvalid("DB123456");
        assertValid("DBSALT123456");
        assertInvalid("DBSALT12345");
        assertInvalid("DBSALT1234567");
        assertValid("APRD12345");
        assertInvalid("APRD1234");
        assertInvalid("APRD123456");
        assertValid("BIOD12345");
        assertInvalid("BIOD1234");
        assertInvalid("BIOD123456");
        assertValid("BTD12345");
        assertInvalid("BTD1234");
        assertInvalid("BTD123456");
        assertValid("EXPT12345");
        assertInvalid("EXPT1234");
        assertInvalid("EXPT123456");
        assertValid("NUTR12345");
        assertInvalid("NUTR1234");
        assertInvalid("NUTR123456");
    }
}
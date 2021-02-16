package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MVStoreIdTest {
    @Test
    void compareTo() {
        final MVStoreId id = new MVStoreId();
        assertEquals(id, new MVStoreId(id.getIdValue()));
        assertEquals(0, id.compareTo(new MVStoreId(id.getIdValue())));
        assertEquals(0, id.compareTo(id.getIdValue()));
    }
}
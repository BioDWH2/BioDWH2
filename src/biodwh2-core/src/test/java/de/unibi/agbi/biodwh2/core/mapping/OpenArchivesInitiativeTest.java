package de.unibi.agbi.biodwh2.core.mapping;

import de.unibi.agbi.biodwh2.core.collections.Tuple2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenArchivesInitiativeTest {
    @Test
    public void isValidIdentifier() {
        // Invalid
        assertFalse(OpenArchivesInitiative.isValidIdentifier(null));
        assertFalse(OpenArchivesInitiative.isValidIdentifier("hello"));
        assertFalse(OpenArchivesInitiative.isValidIdentifier("something:arXiv.org:hep-th/9901001"));
        assertFalse(OpenArchivesInitiative.isValidIdentifier("oai:999:abc123"));
        assertFalse(OpenArchivesInitiative.isValidIdentifier("oai:wibble:abc123"));
        assertFalse(OpenArchivesInitiative.isValidIdentifier("oai:wibble.org:ab cd"));
        assertFalse(OpenArchivesInitiative.isValidIdentifier("oai:wibble.org:ab#cd"));
        assertFalse(OpenArchivesInitiative.isValidIdentifier("oai:wibble.org:ab<cd"));
        assertFalse(OpenArchivesInitiative.isValidIdentifier("oai:wibble.org:ab%3ccd"));
        // Valid
        assertTrue(OpenArchivesInitiative.isValidIdentifier("oai:arXiv.org:hep-th/9901001"));
        assertTrue(OpenArchivesInitiative.isValidIdentifier("oai:foo.org:some-local-id-53"));
        assertTrue(OpenArchivesInitiative.isValidIdentifier("oai:FOO.ORG:some-local-id-53"));
        assertTrue(OpenArchivesInitiative.isValidIdentifier("oai:foo.org:some-local-id-54"));
        assertTrue(OpenArchivesInitiative.isValidIdentifier("oai:foo.org:Some-Local-Id-54"));
        assertTrue(OpenArchivesInitiative.isValidIdentifier("oai:wibble.org:ab%20cd"));
        assertTrue(OpenArchivesInitiative.isValidIdentifier("oai:wibble.org:ab?cd"));
    }

    @Test
    public void buildIdentifier() {
        // Invalid
        assertNull(OpenArchivesInitiative.buildIdentifier(null, null));
        assertNull(OpenArchivesInitiative.buildIdentifier("test", null));
        assertNull(OpenArchivesInitiative.buildIdentifier("wibble", "abc123"));
        // Valid
        assertEquals("oai:wibble.org:ab_cd", OpenArchivesInitiative.buildIdentifier("wibble.org", "ab_cd"));
        assertEquals("oai:wibble.org:ab?cd", OpenArchivesInitiative.buildIdentifier("wibble.org", "ab?cd"));
        assertEquals("oai:wibble.org:ab%3Fcd", OpenArchivesInitiative.buildIdentifier("wibble.org", "ab?cd", true));
    }

    @Test
    public void escape() {
        assertEquals("test", OpenArchivesInitiative.escape("test"));
        assertEquals("ab%20cd", OpenArchivesInitiative.escape("ab cd"));
        assertEquals("ab%20%2Fc%3Fd", OpenArchivesInitiative.escape("ab /c?d"));
    }

    @Test
    public void parseIdentifier() {
        assertNull(OpenArchivesInitiative.parseIdentifier(null));
        assertNull(OpenArchivesInitiative.parseIdentifier("test"));
        assertEquals(new Tuple2<>("arXiv.org", "hep-th/9901001"),
                     OpenArchivesInitiative.parseIdentifier("oai:arXiv.org:hep-th/9901001"));
        assertEquals(new Tuple2<>("wibble.org", "ab?cd"),
                     OpenArchivesInitiative.parseIdentifier("oai:wibble.org:ab?cd"));

    }
}
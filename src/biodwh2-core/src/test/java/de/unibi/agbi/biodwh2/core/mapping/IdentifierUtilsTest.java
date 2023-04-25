package de.unibi.agbi.biodwh2.core.mapping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierUtilsTest {
    @Test
    void isCasNumber() {
        assertFalse(IdentifierUtils.isCasNumber(null));
        assertFalse(IdentifierUtils.isCasNumber(""));
        assertFalse(IdentifierUtils.isCasNumber("not a cas number"));
        assertFalse(IdentifierUtils.isCasNumber("999998732-18-5"));
        assertTrue(IdentifierUtils.isCasNumber("7732-18-5"));
        assertFalse(IdentifierUtils.isCasNumber("8732-18-5"));
    }

    @Test
    void extractDOIs() {
        assertNull(IdentifierUtils.extractDOIs(null));
        assertArrayEquals(new String[]{"10.1515/jib-2020-0033"}, IdentifierUtils.extractDOIs(
                "Friedrichs M. BioDWH2: an automated graph-based data warehouse and mapping tool. Journal of Integrative Bioinformatics. 2021;18(2):167-176. DOI: 10.1515/jib-2020-0033"));
        assertArrayEquals(new String[]{"10.1515/jib-2020-0033"}, IdentifierUtils.extractDOIs("10.1515/jib-2020-0033"));
        assertArrayEquals(new String[]{"10.1007/978-981-16-6795-4_5"}, IdentifierUtils.extractDOIs(
                "Friedrichs M. Automation in graph-based data integration and mapping. In: Chen M, Hofestädt R, eds. Integrative Bioinformatics. Springer Singapore; 2022:97-110. DOI: 10.1007/978-981-16-6795-4_5"));
        assertArrayEquals(new String[]{"10.1517/13543784.17.3.413"}, IdentifierUtils.extractDOIs(
                "Evering TH, Markowitz M: Raltegravir: an integrase inhibitor for HIV-1. Expert Opin Investig Drugs. 2008 Mar;17(3):413-22. doi: 10.1517/13543784.17.3.413 ."));
    }
}
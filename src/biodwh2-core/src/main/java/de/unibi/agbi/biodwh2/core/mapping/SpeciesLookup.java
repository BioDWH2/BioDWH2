package de.unibi.agbi.biodwh2.core.mapping;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public final class SpeciesLookup {
    public static final Entry HOMO_SAPIENS = new Entry("hsa", "Homo sapiens", 9606, "");
    public static final Entry MACACA_MULATTA = new Entry("mcc", "Macaca mulatta", 9544, "MMU");
    public static final Entry MUS_MUSCULUS = new Entry("mmu", "Mus musculus", 10090, "MUS");
    public static final Entry RATTUS_NORVEGICUS = new Entry("rno", "Rattus norvegicus", 10116, "RNO");
    public static final Entry DANIO_RERIO = new Entry("dre", "Danio rerio", 7955, "DAR");
    public static final Entry GALLUS_GALLUS = new Entry("gga", "Gallus gallus", 9031, "GAL");
    
    private static final Entry[] ENTRIES = new Entry[]{
            HOMO_SAPIENS, MACACA_MULATTA, MUS_MUSCULUS, RATTUS_NORVEGICUS, DANIO_RERIO, GALLUS_GALLUS
    };

    private static final Map<String, Entry> scientificNameLookup = new HashMap<>();
    private static final Map<Integer, Entry> ncbiTaxIdLookup = new HashMap<>();

    static {
        for (final Entry entry : ENTRIES) {
            scientificNameLookup.put(entry.scientificName, entry);
            ncbiTaxIdLookup.put(entry.ncbiTaxId, entry);
        }
    }

    public static Entry getByScientificName(final String scientificName) {
        return scientificNameLookup.get(scientificName);
    }

    public static Entry getByNCBITaxId(final Integer ncbiTaxId) {
        return ncbiTaxIdLookup.get(ncbiTaxId);
    }

    public static class Entry {
        public final String keggGenomeAbbreviation;
        public final String scientificName;
        public final Integer ncbiTaxId;
        public final String ensemblGenomeAbbreviation;

        public Entry(final String keggGenomeAbbreviation, final String scientificName, final Integer ncbiTaxId,
                     final String ensemblGenomeAbbreviation) {
            this.keggGenomeAbbreviation = keggGenomeAbbreviation;
            this.scientificName = scientificName;
            this.ncbiTaxId = ncbiTaxId;
            this.ensemblGenomeAbbreviation = ensemblGenomeAbbreviation;
        }
    }
}

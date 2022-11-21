package de.unibi.agbi.biodwh2.core.mapping;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public final class SpeciesLookup {
    public static final Entry ARABIDOPSIS_THALIANA = new Entry(null, "Arabidopsis thaliana", 3702, null);
    public static final Entry BOMBYX_MORI = new Entry(null, "Bombyx mori", 7091, null);
    public static final Entry BOS_TAURUS = new Entry(null, "Bos taurus", 9913, null);
    public static final Entry CAENORHABDITIS_ELEGANS = new Entry(null, "Caenorhabditis elegans", 6239, null);
    public static final Entry CANIS_FAMILIARIS = new Entry(null, "Canis familiaris", 9615, null);
    public static final Entry CAPRA_HIRCUS = new Entry(null, "Capra hircus", 9925, null);
    public static final Entry CRICETULUS_GRISEUS = new Entry(null, "Cricetulus griseus", 10029, null);
    public static final Entry DANIO_RERIO = new Entry("dre", "Danio rerio", 7955, "DAR");
    public static final Entry DROSOPHILA_MELANOGASTER = new Entry(null, "Drosophila melanogaster", 7227, null);
    public static final Entry EPSTEIN_BARR_VIRUS = new Entry(null, "Epstein Barr virus", 10376, null);
    public static final Entry EQUUS_CABALLUS = new Entry(null, "Equus caballus", 9796, null);
    public static final Entry GALLID_ALPHAHERPESVIRUS_2 = new Entry(null, "Gallid alphaherpesvirus 2", 10390, null);
    public static final Entry GALLUS_GALLUS = new Entry("gga", "Gallus gallus", 9031, "GAL");
    public static final Entry GLYCINE_MAX = new Entry(null, "Glycine max", 3847, null);
    public static final Entry GORILLA_GORILLA = new Entry(null, "Gorilla gorilla", 9593, null);
    public static final Entry HOMO_SAPIENS = new Entry("hsa", "Homo sapiens", 9606, "");
    public static final Entry HUMAN_CYTOMEGALOVIRUS = new Entry(null, "Human cytomegalovirus", 10359, null);
    // Acronym: KSHV
    public static final Entry KAPOSI_SARCOMA_ASSOCIATED_HERPESVIRUS = new Entry(null,
                                                                                "Kaposi sarcoma-associated herpesvirus",
                                                                                37296, null);
    public static final Entry MACACA_MULATTA = new Entry("mcc", "Macaca mulatta", 9544, "MMU");
    public static final Entry MACACA_NEMESTRINA = new Entry(null, "Macaca nemestrina", 9545, null);
    public static final Entry MEDICAGO_TRUNCATULA = new Entry(null, "Medicago truncatula", 3880, null);
    public static final Entry MUS_MUSCULUS = new Entry("mmu", "Mus musculus", 10090, "MUS");
    public static final Entry ORYZA_SATIVA = new Entry(null, "Oryza sativa", 4530, null);
    public static final Entry ORYZIAS_LATIPES = new Entry(null, "Oryzias latipes", 8090, null);
    public static final Entry OVIS_ARIES = new Entry(null, "Ovis aries", 9940, null);
    public static final Entry PAN_PANISCUS = new Entry(null, "Pan paniscus", 9597, null);
    public static final Entry PAN_TROGLODYTES = new Entry(null, "Pan troglodytes", 9598, null);
    public static final Entry PHYSCOMITRELLA_PATENS = new Entry(null, "Physcomitrella patens", 3218, null);
    public static final Entry PONGO_PYGMAEUS = new Entry(null, "Pongo pygmaeus", 9600, null);
    public static final Entry RATTUS_NORVEGICUS = new Entry("rno", "Rattus norvegicus", 10116, "RNO");
    public static final Entry SOLANUM_LYCOPERSICUM = new Entry(null, "Solanum lycopersicum", 4081, null);
    public static final Entry SUS_SCROFA = new Entry(null, "Sus scrofa", 9823, null);
    public static final Entry TAENIOPYGIA_GUTTATA = new Entry(null, "Taeniopygia guttata", 59729, null);
    public static final Entry VESICULAR_STOMATITIS_INDIANA_VIRUS = new Entry(null, "Vesicular stomatitis Indiana virus",
                                                                             null, null);
    public static final Entry XENOPUS_LAEVIS = new Entry(null, "Xenopus laevis", 8355, null);
    public static final Entry XENOPUS_TROPICALIS = new Entry(null, "Xenopus tropicalis", 8364, null);
    public static final Entry ZEA_MAYS = new Entry(null, "Zea mays", 4577, null);

    private static final Entry[] ENTRIES = new Entry[]{
            ARABIDOPSIS_THALIANA, BOMBYX_MORI, BOS_TAURUS, CAENORHABDITIS_ELEGANS, CANIS_FAMILIARIS, CAPRA_HIRCUS,
            CRICETULUS_GRISEUS, DANIO_RERIO, DROSOPHILA_MELANOGASTER, EPSTEIN_BARR_VIRUS, EQUUS_CABALLUS,
            GALLID_ALPHAHERPESVIRUS_2, GALLUS_GALLUS, GLYCINE_MAX, GORILLA_GORILLA, HOMO_SAPIENS, HUMAN_CYTOMEGALOVIRUS,
            KAPOSI_SARCOMA_ASSOCIATED_HERPESVIRUS, MACACA_MULATTA, MACACA_NEMESTRINA, MEDICAGO_TRUNCATULA, MUS_MUSCULUS,
            ORYZA_SATIVA, ORYZIAS_LATIPES, OVIS_ARIES, PAN_PANISCUS, PAN_TROGLODYTES, PHYSCOMITRELLA_PATENS,
            PONGO_PYGMAEUS, RATTUS_NORVEGICUS, SOLANUM_LYCOPERSICUM, SUS_SCROFA, TAENIOPYGIA_GUTTATA,
            VESICULAR_STOMATITIS_INDIANA_VIRUS, XENOPUS_LAEVIS, XENOPUS_TROPICALIS, ZEA_MAYS
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

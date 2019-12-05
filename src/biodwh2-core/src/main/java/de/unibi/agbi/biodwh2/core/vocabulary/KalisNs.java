package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class KalisNs {
    private static final String Uri = "https://rdf.kalis-amts.de/ns#";

    private static final Model m = ModelFactory.createDefaultModel();

    private static Property createHGNCProperty(String property) {
        return m.createProperty(Uri + "hgnc/" + property);
    }

    private static Property createProperty(String property) {
        return m.createProperty(Uri + property);
    }

    public static Property locationSortableHGNCProperty;
    public static Property aliasSymbolHGNCProperty;
    public static Property enaHGNCProperty;
    public static Property lsdbHGNCProperty;
    public static Property mirbaseHGNCProperty;
    public static Property homeodbHGNCProperty;
    public static Property bioparadigmsSlcHGNCProperty;
    public static Property mamitTrnadbHGNCProperty;
    public static Property lncrnadbHGNCProperty;
    public static Property rnaCentralIdsHGNCProperty;
    public static Property lncipediaHGNCProperty;
    public static Property gtrnadbHGNCProperty;

    private static Property locationSortableProperty;
    private static Property aliasSymbolProperty;
    private static Property enaProperty;
    private static Property lsdbProperty;
    private static Property mirbaseProperty;
    private static Property homeodbProperty;
    private static Property bioparadigmsSlcProperty;
    private static Property mamitTrnadbProperty;
    private static Property lncrnadbProperty;
    private static Property rnaCentralIdsProperty;
    private static Property lncipediaProperty;
    private static Property gtrnadbProperty;

    static {
        locationSortableProperty = createProperty("locationSortable");
        KalisNs.locationSortableProperty.addProperty(RDFS.comment, "locations sortable");
        aliasSymbolProperty = createProperty("aliasSymbol");
        KalisNs.aliasSymbolProperty.addProperty(RDFS.comment, "Other symbols used to refer to this gene.");
        enaProperty = createProperty("ena");
        KalisNs.enaProperty.addProperty(RDFS.comment, "INSDC nucleotide sequence accession numbers selected " +
                                                      "by the HGNC for a gene.");
        lsdbProperty = createProperty("lsdb");
        KalisNs.lsdbProperty.addProperty(RDFS.comment, "This contains LSDB database names/URL pertinent to the gene.");
        mirbaseProperty = createProperty("mirbase");
        KalisNs.mirbaseProperty.addProperty(RDFS.comment, "An accession number for a microRNA sequence within the " +
                                                          "miRBase database for the HGNC gene.");
        homeodbProperty = createProperty("homedb");
        KalisNs.homeodbProperty.addProperty(RDFS.comment,
                                            "ID for a homeobox gene within the Homeobox database (HomeoDB2).");
        bioparadigmsSlcProperty = createProperty("bioparadigmsSlc");
        KalisNs.bioparadigmsSlcProperty.addProperty(RDFS.comment,
                                                    "The gene symbol for a solute carrier gene as found " +
                                                    "in the Bioparadigms SLC tables database.");

        mamitTrnadbProperty = createProperty("mamitTrnadb");
        KalisNs.mamitTrnadbProperty.addProperty(RDFS.comment,
                                                "Mamit-tRNAdb is a compilation of mammalian mitochondrial " +
                                                "tRNA genes. The ID refers to a tRNA gene within the " +
                                                "mamit-tRNAdb database.");
        lncrnadbProperty = createProperty("lncrnadb");
        KalisNs.lncrnadbProperty.addProperty(RDFS.comment,
                                             "lncRNAdb: a biological database of Long " + "non-coding RNAs.");
        rnaCentralIdsProperty = createProperty("rnaCentralIds");
        KalisNs.rnaCentralIdsProperty.addProperty(RDFS.comment,
                                                  "RNAcentral: The non-coding RNA sequence" + " database.");
        lncipediaProperty = createProperty("lncipedia");
        KalisNs.lncipediaProperty.addProperty(RDFS.comment, "LNCipedia: A comprehensive compendium of human " +
                                                            "long non-coding RNA's.");
        gtrnadbProperty = createProperty("gtrnadb");
        KalisNs.gtrnadbProperty.addProperty(RDFS.comment, "GtRNAdb: Genomic tRNA Database");

        locationSortableHGNCProperty = createHGNCProperty("locationSortable");
        KalisNs.locationSortableHGNCProperty.addProperty(OWL.sameAs, locationSortableProperty);
        aliasSymbolHGNCProperty = createHGNCProperty("aliasSymbol");
        KalisNs.aliasSymbolHGNCProperty.addProperty(OWL.sameAs, aliasSymbolProperty);
        enaHGNCProperty = createHGNCProperty("ena");
        KalisNs.enaHGNCProperty.addProperty(OWL.sameAs, enaProperty);
        lsdbHGNCProperty = createHGNCProperty("lsdb");
        KalisNs.lsdbHGNCProperty.addProperty(OWL.sameAs, lsdbProperty);
        mirbaseHGNCProperty = createHGNCProperty("mirbase");
        KalisNs.mirbaseHGNCProperty.addProperty(OWL.sameAs, mirbaseProperty);
        homeodbHGNCProperty = createHGNCProperty("homedb");
        KalisNs.homeodbHGNCProperty.addProperty(OWL.sameAs, homeodbProperty);
        bioparadigmsSlcHGNCProperty = createHGNCProperty("bioparadigmsSlc");
        KalisNs.bioparadigmsSlcHGNCProperty.addProperty(OWL.sameAs, bioparadigmsSlcProperty);
        mamitTrnadbHGNCProperty = createHGNCProperty("mamitTrnadb");
        KalisNs.mamitTrnadbHGNCProperty.addProperty(OWL.sameAs, mamitTrnadbProperty);
        lncrnadbHGNCProperty = createHGNCProperty("lncrnadb");
        KalisNs.lncrnadbHGNCProperty.addProperty(OWL.sameAs, lncrnadbProperty);
        rnaCentralIdsHGNCProperty = createHGNCProperty("rnaCentralIds");
        KalisNs.rnaCentralIdsHGNCProperty.addProperty(OWL.sameAs, rnaCentralIdsProperty);
        lncipediaHGNCProperty = createHGNCProperty("lncipedia");
        KalisNs.lncipediaHGNCProperty.addProperty(OWL.sameAs, lncipediaProperty);
        gtrnadbHGNCProperty = createHGNCProperty("gtrnadb");
        KalisNs.gtrnadbHGNCProperty.addProperty(OWL.sameAs, gtrnadbHGNCProperty);
    }

    private KalisNs() {
    }
}

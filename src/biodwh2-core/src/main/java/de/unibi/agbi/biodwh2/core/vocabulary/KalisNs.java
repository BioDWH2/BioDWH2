package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class KalisNs {
    private static final String Uri = "https://rdf.kalis-amts.de/ns#";

    private static final Model m = ModelFactory.createDefaultModel();

    private static void createProperty(Property propName, String comment) {
        Property prop = m.createProperty(Uri + propName);
        prop.addProperty(RDFS.comment, comment);
        Property propHGNC = m.createProperty(Uri + "hgnc/" + propName);
        propHGNC.addProperty(OWL.sameAs, prop);
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
        createProperty(locationSortableProperty, "locations sortable");
        createProperty(aliasSymbolProperty, "Other symbols used to refer to this gene.");
        createProperty(enaProperty,
                       "INSDC nucleotide sequence accession numbers selected " + "by the HGNC for a gene.");
        createProperty(lsdbProperty, "This contains LSDB database names/URL pertinent to the gene.");
        createProperty(mirbaseProperty, "An accession number for a microRNA sequence within the " +
                                        "miRBase database for the HGNC gene.");
        createProperty(homeodbProperty, "ID for a homeobox gene within the Homeobox database (HomeoDB2).");
        createProperty(bioparadigmsSlcProperty, "The gene symbol for a solute carrier gene as found " +
                                                "in the Bioparadigms SLC tables database.");
        createProperty(mamitTrnadbProperty, "Mamit-tRNAdb is a compilation of mammalian mitochondrial " +
                                            "tRNA genes. The ID refers to a tRNA gene within the " +
                                            "mamit-tRNAdb database.");
        createProperty(lncrnadbProperty, "lncRNAdb: a biological database of Long " + "non-coding RNAs.");
        createProperty(rnaCentralIdsProperty, "RNAcentral: The non-coding RNA sequence database.");
        createProperty(lncipediaProperty, "LNCipedia: A comprehensive compendium of human " + "long non-coding RNA's.");
        createProperty(gtrnadbProperty, "GtRNAdb: Genomic tRNA Database");
    }

    private KalisNs() {
    }
}

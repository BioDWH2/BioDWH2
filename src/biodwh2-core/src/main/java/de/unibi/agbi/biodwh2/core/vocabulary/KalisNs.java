package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.RDFS;

public class KalisNs {
    private static final String Uri = "https://rdf.kalis-amts.de/ns#";

    private static final Model m = ModelFactory.createDefaultModel();

    private static Property createHGNCProperty(String property) {
        return m.createProperty(Uri + "hgnc/" + property);
    }


    public static Property locusGroupHGNCProperty;
    public static Property locusTypeHGNCProperty;
    public static Property locationHGNCProperty;
    public static Property locationSortableHGNCProperty;
    public static Property aliasSymbolHGNCProperty;
    public static Property aliasNameHGNCProperty;
    public static Property prevSymbolHGNCProperty;
    public static Property prevNameHGNCProperty;
    public static Property geneFamilyHGNCProperty;
    public static Property geneFamilyIdHGNCProperty;
    public static Property dateApprovedReservedHGNCProperty;
    public static Property dateSymbolChangedHGNCProperty;
    public static Property dateNameChangedHGNCProperty;
    public static Property dateModifiedHGNCProperty;
    public static Property entrezIdHGNCProperty;
    public static Property ensembleGeneIdHGNCProperty;
    public static Property vegaIdHGNCProperty;
    public static Property ucscIdHGNCProperty;
    public static Property enaHGNCProperty;
    public static Property refsecAccessionHGNCProperty;
    public static Property ccdsIdHGNCProperty;
    public static Property uniprotIdsHGNCProperty;
    public static Property pubmedIdHGNCProperty;
    public static Property mgdIdHGNCProperty;
    public static Property rgdIdHGNCProperty;
    public static Property lsdbHGNCProperty;
    public static Property cosmicHGNCProperty;
    public static Property omimIdHGNCProperty;
    public static Property mirbaseHGNCProperty;
    public static Property homeodbHGNCProperty;
    public static Property snornabaseHGNCProperty;
    public static Property bioparadigmsSlcHGNCProperty;
    public static Property orphanetHGNCProperty;
    public static Property pseudogeneHGNCProperty;
    public static Property hordeIdHGNCProperty;
    public static Property meropsHGNCProperty;
    public static Property imgtHGNCProperty;
    public static Property iupharHGNCProperty;
    public static Property kznfGeneCatalogHGNCProperty;
    public static Property mamitTrnadbHGNCProperty;
    public static Property cdHGNCProperty;
    public static Property lncrnadbHGNCProperty;
    public static Property enzymeIdHGNCProperty;
    public static Property intermediateFilamentDbHGNCProperty;
    public static Property rnaCentralIdsHGNCProperty;
    public static Property lncipediaHGNCProperty;
    public static Property gtrnadbHGNCProperty;

    static {
        locusGroupHGNCProperty = createHGNCProperty("locusGroup");
        KalisNs.locusGroupHGNCProperty.addProperty(RDFS.comment, "Groups locus types together into related sets, " +
                                                                 "e.g. protein-coding gene, non-coding RNA etc.");
        locusTypeHGNCProperty = createHGNCProperty("locusType");
        KalisNs.locusTypeHGNCProperty.addProperty(RDFS.comment,
                                                  "Specifies the type of locus described by the given entry: " +
                                                  "e.g. complex locus constituent, endogenous retrovirus etc.");
        locationHGNCProperty = createHGNCProperty("location");
        KalisNs.locationHGNCProperty.addProperty(RDFS.comment,
                                                 "Indicates the location of the gene or region on the chromosome");
        locationSortableHGNCProperty = createHGNCProperty("locationSortable");
        KalisNs.locationSortableHGNCProperty.addProperty(RDFS.comment, "locations sortable");
        aliasSymbolHGNCProperty = createHGNCProperty("aliasSymbol");
        KalisNs.aliasSymbolHGNCProperty.addProperty(RDFS.comment, "Other symbols used to refer to this gene.");
        aliasNameHGNCProperty = createHGNCProperty("aliasName");
        KalisNs.aliasNameHGNCProperty.addProperty(RDFS.comment, "Other names used to refer to this gene.");
        prevSymbolHGNCProperty = createHGNCProperty("prevSymbol");
        KalisNs.prevSymbolHGNCProperty.addProperty(RDFS.comment,
                                                   "Symbols previously approved by the HGNC for this gene.");
        prevNameHGNCProperty = createHGNCProperty("prevName");
        KalisNs.prevNameHGNCProperty.addProperty(RDFS.comment,
                                                 "Gene names previously approved by the HGNC for this gene.");
        geneFamilyHGNCProperty = createHGNCProperty("geneFamily");
        KalisNs.geneFamilyHGNCProperty.addProperty(RDFS.comment,
                                                   "The name given/chosen by the HGNC for the family/group.");
        geneFamilyIdHGNCProperty = createHGNCProperty("geneFamilyId");
        KalisNs.geneFamilyIdHGNCProperty.addProperty(RDFS.comment,
                                                     "Each gene family/group has a unique numerical ID that" +
                                                     " forms the last part of the gene family/group page URL to " +
                                                     "aid linking and downloading.");
        dateApprovedReservedHGNCProperty = createHGNCProperty("dateApprovedReserved");
        KalisNs.dateApprovedReservedHGNCProperty.addProperty(RDFS.comment,
                                                             "Date the gene symbol and name were approved by " +
                                                             "the HGNC.");
        dateSymbolChangedHGNCProperty = createHGNCProperty("dateSymbolChanged");
        KalisNs.dateSymbolChangedHGNCProperty.addProperty(RDFS.comment,
                                                          "If applicable, the date the approved gene symbol was " +
                                                          "last changed by the HGNC.");
        dateNameChangedHGNCProperty = createHGNCProperty("dateNameChanged");
        KalisNs.dateNameChangedHGNCProperty.addProperty(RDFS.comment,
                                                        "If applicable, the date the approved gene symbol was " +
                                                        "last changed by the HGNC.");
        dateModifiedHGNCProperty = createHGNCProperty("dateModified");
        KalisNs.dateModifiedHGNCProperty.addProperty(RDFS.comment,
                                                     "If applicable, the date the entry was modified by the HGNC.");
        entrezIdHGNCProperty = createHGNCProperty("entrezId");
        KalisNs.entrezIdHGNCProperty.addProperty(RDFS.comment, "Entrez gene ID");
        ensembleGeneIdHGNCProperty = createHGNCProperty("ensembleGeneId");
        KalisNs.ensembleGeneIdHGNCProperty.addProperty(RDFS.comment,
                                                       "The Ensembl gene ID associated with the HGNC gene symbol. " +
                                                       "The Ensembl project produces genome databases for " +
                                                       "vertebrates and other eukaryotic species.");
        vegaIdHGNCProperty = createHGNCProperty("vegaId");
        KalisNs.vegaIdHGNCProperty.addProperty(RDFS.comment, "The Vega gene ID associated with the HGNC gene " +
                                                             "symbol. The VEGA database is a repository for " +
                                                             "high-quality gene models produced by the manual " +
                                                             "annotation of vertebrate genomes.");
        ucscIdHGNCProperty = createHGNCProperty("uscsId");
        KalisNs.ucscIdHGNCProperty.addProperty(RDFS.comment, "The UCSC gene ID associated with the HGNC gene " +
                                                             "symbol. The ID is used within the UCSC genome browser" +
                                                             " to identify an annotated human gene record within the " +
                                                             "UCSC genome browser. ");
        enaHGNCProperty = createHGNCProperty("ena");
        KalisNs.enaHGNCProperty.addProperty(RDFS.comment, "INSDC nucleotide sequence accession numbers selected " +
                                                          "by the HGNC for a gene.");
        refsecAccessionHGNCProperty = createHGNCProperty("refsecAccession");
        KalisNs.refsecAccessionHGNCProperty.addProperty(RDFS.comment,
                                                        "The Reference Sequence (RefSeq) identifier displayed " +
                                                        "within the HGNC gene symbol report. RefSeq aims to provide " +
                                                        "a comprehensive, integrated, non-redundant set of sequences" +
                                                        ", including genomic DNA, transcript (RNA), and protein " +
                                                        "products. RefSeq identifiers are designed to provide a " +
                                                        "stable reference for gene identification and " +
                                                        "characterization, mutation analysis, expression studies, " +
                                                        "polymorphism discovery, and comparative analyses.");
        ccdsIdHGNCProperty = createHGNCProperty("ccdsId");
        KalisNs.ccdsIdHGNCProperty.addProperty(RDFS.comment,
                                               "The Consensus CDS (CCDS) project is a collaborative effort to " +
                                               "identify a core set of human and mouse protein coding regions that " +
                                               "are consistently annotated and of high quality. The long term goal " +
                                               "is to support convergence towards a standard set of gene annotations.");
        uniprotIdsHGNCProperty = createHGNCProperty("uniprotId");
        KalisNs.uniprotIdsHGNCProperty.addProperty(RDFS.comment,
                                                   "The UniProt identifier for a protein product of the gene. " +
                                                   "The UniProt Protein Knowledgebase is described as a curated " +
                                                   "protein sequence database that provides a high level of " +
                                                   "annotation, a minimal level of redundancy and high level of " +
                                                   "integration with other databases.");
        pubmedIdHGNCProperty = createHGNCProperty("pubmedId");
        KalisNs.pubmedIdHGNCProperty.addProperty(RDFS.comment,
                                                 "Identifier that links to published articles relevant to the " +
                                                 "gene in the NCBI's PubMed database.");
        mgdIdHGNCProperty = createHGNCProperty("mgdId");
        KalisNs.mgdIdHGNCProperty.addProperty(RDFS.comment,
                                              "Mouse Genome Informatics ID for the mouse homologs of the human " +
                                              "gene.");
        rgdIdHGNCProperty = createHGNCProperty("rgdId");
        KalisNs.rgdIdHGNCProperty.addProperty(RDFS.comment,
                                              "Rat Genome Database ID for the rat homologs of the human gene.");
        lsdbHGNCProperty = createHGNCProperty("lsdb");
        KalisNs.lsdbHGNCProperty.addProperty(RDFS.comment,
                                             "This contains LSDB database names/URL pertinent to the gene.");
        cosmicHGNCProperty = createHGNCProperty("cosmic");
        KalisNs.cosmicHGNCProperty.addProperty(RDFS.comment,
                                               "The gene symbol displayed within the Catalogue Of Somatic " +
                                               "Mutations In Cancer (Cosmic). Most of the gene symbols will be " +
                                               "the same as HGNC approved gene symbol but for some genes in Cosmic " +
                                               "this may not be the case. ");
        omimIdHGNCProperty = createHGNCProperty("omimId");
        KalisNs.omimIdHGNCProperty.addProperty(RDFS.comment,
                                               "Identifier provided by Online Mendelian Inheritance in Man " +
                                               "(OMIM). This database is described as a catalog of human genes and " +
                                               "genetic disorders containing textual information and links to " +
                                               "additional related resources.");
        mirbaseHGNCProperty = createHGNCProperty("mirbase");
        KalisNs.mirbaseHGNCProperty.addProperty(RDFS.comment,
                                                "An accession number for a microRNA sequence within the " +
                                                "miRBase database for the HGNC gene.");
        homeodbHGNCProperty = createHGNCProperty("homedb");
        KalisNs.homeodbHGNCProperty.addProperty(RDFS.comment,
                                                "ID for a homeobox gene within the Homeobox database (HomeoDB2).");
        snornabaseHGNCProperty = createHGNCProperty("snornabase");
        KalisNs.snornabaseHGNCProperty.addProperty(RDFS.comment,
                                                   "snoRNABase is a comprehensive database of human H/ACA and " +
                                                   "C/D box snoRNAs. The ID itself refers to a snoRNA page " +
                                                   "within the database resource.");
        bioparadigmsSlcHGNCProperty = createHGNCProperty("bioparadigmsSlc");
        KalisNs.bioparadigmsSlcHGNCProperty.addProperty(RDFS.comment,
                                                        "The gene symbol for a solute carrier gene as found " +
                                                        "in the Bioparadigms SLC tables database.");
        orphanetHGNCProperty = createHGNCProperty("orphanet");
        KalisNs.orphanetHGNCProperty.addProperty(RDFS.comment,
                                                 "The Orphanet ID identifies a gene within orphanet and the " +
                                                 "rare diseases that are associated to the gene.");
        pseudogeneHGNCProperty = createHGNCProperty("pseudogene");
        KalisNs.pseudogeneHGNCProperty.addProperty(RDFS.comment, "An ID for a pseudogene entry/sequence within the " +
                                                                 "Pseudogene.org database.");
        hordeIdHGNCProperty = createHGNCProperty("hordeId");
        KalisNs.hordeIdHGNCProperty.addProperty(RDFS.comment,
                                                "The ID for an olfactory receptor gene entry within the Human " +
                                                "Olfactory Receptor Data Exploratorium (HORDE) database.");
        meropsHGNCProperty = createHGNCProperty("merops");
        KalisNs.meropsHGNCProperty.addProperty(RDFS.comment,
                                               "The MEROPS database is an information resource for peptidases " +
                                               "(also termed proteases, proteinases and proteolytic enzymes) and " +
                                               "the proteins that inhibit them.");
        imgtHGNCProperty = createHGNCProperty("imgt");
        KalisNs.imgtHGNCProperty.addProperty(RDFS.comment,
                                             "The IMGT/GENE-DB gene symbol for immunoglobulin and T-cell " +
                                             "receptor genes associated to the HGNC gene. The gene symbols are " +
                                             "either the same as, or equivalent to, HGNC approved gene symbols. " +
                                             "Equivalent IMGT symbols include the character \"/\" which is not " +
                                             "present in HGNC approved symbols. The presence of an IMGT gene symbol " +
                                             "indicates that the gene can be found within the IMGT/GENE-DB.");
        iupharHGNCProperty = createHGNCProperty("iuphar");
        KalisNs.iupharHGNCProperty.addProperty(RDFS.comment,
                                               "IUPHAR/BPS Guide to PHARMACOLOGY is an expert-driven guide " +
                                               "to pharmacological targets and the substances that act on them. " +
                                               "The ID is their object ID that is used as an identifier for a gene " +
                                               "record within their database.");
        kznfGeneCatalogHGNCProperty = createHGNCProperty("kznfGeneCatalog");
        KalisNs.kznfGeneCatalogHGNCProperty.addProperty(RDFS.comment,
                                                        "The KZNF catalog is a comprehensive collection of " +
                                                        "Krüppel-type zinc finger genes (KZNFs) in primates with " +
                                                        "finished or high quality draft genomes. The ID refers to a " +
                                                        "gene report within the KZNF catalog.");
        mamitTrnadbHGNCProperty = createHGNCProperty("mamitTrnadb");
        KalisNs.mamitTrnadbHGNCProperty.addProperty(RDFS.comment,
                                                    "Mamit-tRNAdb is a compilation of mammalian mitochondrial " +
                                                    "tRNA genes. The ID refers to a tRNA gene within the " +
                                                    "mamit-tRNAdb database.");
        cdHGNCProperty = createHGNCProperty("cd");
        KalisNs.cdHGNCProperty.addProperty(RDFS.comment,
                                           "The CD name for a cellular differentiation molecule found within " +
                                           "the HCDM database.");
        lncrnadbHGNCProperty = createHGNCProperty("lncrnadb");
        KalisNs.lncrnadbHGNCProperty.addProperty(RDFS.comment,
                                                 "lncRNAdb: a biological database of Long " + "non-coding RNAs.");
        enzymeIdHGNCProperty = createHGNCProperty("enzymeId");
        KalisNs.enzymeIdHGNCProperty.addProperty(RDFS.comment, "Enzyme nomenclature database.");
        intermediateFilamentDbHGNCProperty = createHGNCProperty("intermediateFilamentDb");
        KalisNs.intermediateFilamentDbHGNCProperty.addProperty(RDFS.comment,
                                                               "Human Intermediate Filament " + "Database.");
        rnaCentralIdsHGNCProperty = createHGNCProperty("rnaCentralIds");
        KalisNs.rnaCentralIdsHGNCProperty.addProperty(RDFS.comment,
                                                      "RNAcentral: The non-coding RNA sequence" + " database.");
        lncipediaHGNCProperty = createHGNCProperty("lncipedia");
        KalisNs.lncipediaHGNCProperty.addProperty(RDFS.comment, "LNCipedia: A comprehensive compendium of human " +
                                                                "long non-coding RNA's.");
        gtrnadbHGNCProperty = createHGNCProperty("gtrnadb");
        KalisNs.gtrnadbHGNCProperty.addProperty(RDFS.comment, "GtRNAdb: Genomic tRNA Database");
    }

    private KalisNs() {
    }
}

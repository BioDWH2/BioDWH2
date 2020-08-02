package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class BioPortal {
    public static final Property ALIASES = ResourceFactory.createProperty("Aliases");
    public static final Property APPROVED_NAME = ResourceFactory.createProperty("Approved_Name");
    public static final Property CCDS_IDS = ResourceFactory.createProperty("CCDS_IDs");
    public static final Property CD_ID = ResourceFactory.createProperty("CD_ID");
    public static final Property CHROMOSOME = ResourceFactory.createProperty("Chromosome");
    public static final Property COSMIC_ID = ResourceFactory.createProperty("COSMIC_ID");
    public static final Property DATE_APPROVED = ResourceFactory.createProperty("Date_Approved");
    public static final Property DATE_MODIFIED = ResourceFactory.createProperty("Date_Modified ");
    public static final Property DATE_NAME_CHANGED = ResourceFactory.createProperty("Date_Name_Changed");
    public static final Property DATE_SYMBOL_CHANGED = ResourceFactory.createProperty("Date_Symbol_Changed");
    public static final Property ENSEMBL_GENE_ID = ResourceFactory.createProperty("Ensembl_Gene_ID");
    public static final Property ENTREZ_GENE_ID = ResourceFactory.createProperty("Entrez_Gene_ID");
    public static final Property ENZYME_IDS = ResourceFactory.createProperty("Enzyme_IDs");
    public static final Property GENE_FAMILY_TAG = ResourceFactory.createProperty("Gene_Family_Tag");
    public static final Property HORDE_ID = ResourceFactory.createProperty("Horde_ID");
    public static final Property IMGT_GENEDB_ID = ResourceFactory.createProperty("IMGT_GENE-DB_ID");
    public static final Property INTERMEDIATE_FILAMENT_DB_ID = ResourceFactory.createProperty(
            "Intermediate_Filament_DB_ID");
    public static final Property IUPHAR_ID = ResourceFactory.createProperty("IUPHAR_ID");
    public static final Property KZNF_GENE_CATALOG_ID = ResourceFactory.createProperty("KZNF_Gene_Catalog_ID");
    public static final Property LOCUS_GROUP = ResourceFactory.createProperty("Locus_Group");
    public static final Property LOCUS_TYPE = ResourceFactory.createProperty("Locus_Type");
    public static final Property MEROPS_ID = ResourceFactory.createProperty("MEROPS_ID");
    public static final Property MOUSE_GENOME_DATABASE_ID = ResourceFactory.createProperty("Mouse_Genome_Database_ID");
    public static final Property ORPHANET_ID = ResourceFactory.createProperty("Orphanet_ID");
    public static final Property PREVIOUS_NAMES = ResourceFactory.createProperty("Previous_Names");
    public static final Property PREVIOUS_SYMBOLS = ResourceFactory.createProperty("Previous_Symbols");
    public static final Property PSEUDOGENE_ID = ResourceFactory.createProperty("Pseudogene_ID");
    public static final Property REF_SEQ_IDS = ResourceFactory.createProperty("RefSeq_IDs");
    public static final Property SNO_RNA_BASE_ID = ResourceFactory.createProperty("snoRNABase_ID");
    public static final Property STATUS = ResourceFactory.createProperty("Status");
    public static final Property VEGA_IDS = ResourceFactory.createProperty("VEGA_IDs");
}

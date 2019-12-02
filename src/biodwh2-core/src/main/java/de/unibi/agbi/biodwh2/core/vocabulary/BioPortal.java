package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class BioPortal {
    private static final String Uri = "http://ncicb.nci.nih.gov/xml/owl/EVS/Hugo.owl#";

    public static final Property accessionNumbers = ResourceFactory.createProperty("Accession_Numbers");
    public static final Property aliases = ResourceFactory.createProperty("Aliases");
    public static final Property approvedName = ResourceFactory.createProperty("Approved_Name");
    public static final Property approvedSymbol = ResourceFactory.createProperty("Approved_Symbol ");
    public static final Property ccdsIDs = ResourceFactory.createProperty("CCDS_IDs");
    public static final Property cdId = ResourceFactory.createProperty("CD_ID");
    public static final Property cdLink = ResourceFactory.createProperty("CD_LINK");
    public static final Property chromosome = ResourceFactory.createProperty("Chromosome");
    public static final Property cosmicId = ResourceFactory.createProperty("COSMIC_ID");
    public static final Property cosmicLink = ResourceFactory.createProperty("COSMIC_LINK");
    public static final Property dateApproved = ResourceFactory.createProperty("Date_Approved");
    public static final Property dateModified = ResourceFactory.createProperty("Date_Modified ");
    public static final Property dateNameChanged = ResourceFactory.createProperty("Date_Name_Changed");
    public static final Property dateSymbolChanged = ResourceFactory.createProperty("Date_Symbol_Changed");
    public static final Property ensemblGeneId = ResourceFactory.createProperty("Ensembl_Gene_ID");
    public static final Property ensemblId__mappedDataSuppliedByEnsembl = ResourceFactory.createProperty(
            "Ensembl_ID__mapped_data_supplied_by_Ensembl_");
    public static final Property entrezGeneId = ResourceFactory.createProperty("Entrez_Gene_ID");
    public static final Property entrezGeneId__mappedDataSuppliedByNcbi = ResourceFactory.createProperty(
            "Entrez_Gene_ID__mapped_data_supplied_by_NCBI_");
    public static final Property enzymeIDs = ResourceFactory.createProperty("Enzyme_IDs");
    public static final Property gdbId__mappedData = ResourceFactory.createProperty("GDB_ID__mapped_data_");
    public static final Property geneFamilyTag = ResourceFactory.createProperty("Gene_Family_Tag");
    public static final Property hgncId = ResourceFactory.createProperty("HGNC_ID");
    public static final Property hordeId = ResourceFactory.createProperty("Horde_ID");
    public static final Property hordeLink = ResourceFactory.createProperty("Horde_LINK");
    public static final Property imgtGenedbId = ResourceFactory.createProperty("IMGT_GENE-DB_ID");
    public static final Property intermediateFilamentDbId = ResourceFactory.createProperty(
            "Intermediate_Filament_DB_ID");
    public static final Property intermediateFilamentDbLink = ResourceFactory.createProperty(
            "Intermediate_Filament_DB_LINK");
    public static final Property iupharId = ResourceFactory.createProperty("IUPHAR_ID");
    public static final Property iupharLink = ResourceFactory.createProperty("IUPHAR_LINK");
    public static final Property kznfGeneCatalogId = ResourceFactory.createProperty("KZNF_Gene_Catalog_ID");
    public static final Property kznfGeneCatalogLink = ResourceFactory.createProperty("KZNF_Gene_Catalog_LINK");
    public static final Property locusGroup = ResourceFactory.createProperty("Locus_Group");
    public static final Property locusSpecificDatabases = ResourceFactory.createProperty("Locus_Specific_Databases");
    public static final Property locusType = ResourceFactory.createProperty("Locus_Type");
    public static final Property meropsId = ResourceFactory.createProperty("MEROPS_ID");
    public static final Property meropsLink = ResourceFactory.createProperty("MEROPS_LINK");
    public static final Property miRNAMiRBaseId = ResourceFactory.createProperty("miRNA_miRBase_ID");
    public static final Property miRNAMiRBaseLink = ResourceFactory.createProperty("miRNA_miRBase_LINK");
    public static final Property mouseGenomeDatabaseId = ResourceFactory.createProperty("Mouse_Genome_Database_ID");
    public static final Property mouseGenomeDatabaseId__mappedDataSuppliedByMgi = ResourceFactory.createProperty(
            "Mouse_Genome_Database_ID__mapped_data_supplied_by_MGI_");
    public static final Property nameAliases = ResourceFactory.createProperty("Name_Aliases");
    public static final Property omimId__mappedDataSuppliedByNcbi = ResourceFactory.createProperty(
            "OMIM_ID__mapped_data_supplied_by_NCBI_");
    public static final Property orphanetId = ResourceFactory.createProperty("Orphanet_ID");
    public static final Property orphanetLink = ResourceFactory.createProperty("Orphanet_LINK");
    public static final Property piRNABankId = ResourceFactory.createProperty("piRNABank_ID");
    public static final Property piRNABankLink = ResourceFactory.createProperty("piRNABank_LINK");
    public static final Property prefixIRI = ResourceFactory.createProperty("prefixIRI");
    public static final Property previousNames = ResourceFactory.createProperty("Previous_Names");
    public static final Property previousSymbols = ResourceFactory.createProperty("Previous_Symbols");
    public static final Property primaryIDs = ResourceFactory.createProperty("Primary_IDs");
    public static final Property pseudogeneId = ResourceFactory.createProperty("Pseudogene_ID");
    public static final Property pseudogeneLink = ResourceFactory.createProperty("Pseudogene_LINK");
    public static final Property ratGenomeDatabaseId__mappedDataSuppliedByRgd = ResourceFactory.createProperty(
            "Rat_Genome_Database_ID__mapped_data_supplied_by_RGD_");
    public static final Property recordType = ResourceFactory.createProperty("Record_Type");
    public static final Property refSeq__mappedDataSuppliedByNcbi = ResourceFactory.createProperty(
            "RefSeq__mapped_data_supplied_by_NCBI_");
    public static final Property refSeqIDs = ResourceFactory.createProperty("RefSeq_IDs");
    public static final Property rfamId = ResourceFactory.createProperty("Rfam_ID");
    public static final Property rfamLink = ResourceFactory.createProperty("Rfam_LINK");
    public static final Property secondaryIDs = ResourceFactory.createProperty("Secondary_IDs");
    public static final Property snoRNABaseId = ResourceFactory.createProperty("snoRNABase_ID");
    public static final Property snoRNABaseLink = ResourceFactory.createProperty("snoRNABase_LINK");
    public static final Property status = ResourceFactory.createProperty("Status");
    public static final Property ucscId__mappedDataSuppliedByUcsc = ResourceFactory.createProperty(
            "UCSC_ID__mapped_data_supplied_by_UCSC_");
    public static final Property uniProtId__mappedDataSuppliedByUniProt = ResourceFactory.createProperty(
            "UniProt_ID__mapped_data_supplied_by_UniProt_");
    public static final Property vegaIDs = ResourceFactory.createProperty("VEGA_IDs");
}

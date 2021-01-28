package de.unibi.agbi.biodwh2.core.model;

public enum IdentifierType {
    DUMMY("Dummy"),
    HGNC_SYMBOL("HGNC_Symbol"),
    HGNC_ID("HGNC"),
    GENE_CARD("GeneCard"),
    GEN_ATLAS("GenAtlas"),
    DRUG_BANK("DrugBank"),
    DRUG_CENTRAL("DrugCentral"),
    PHARM_GKB("PharmGKB"),
    KEGG("KEGG"),
    UNII("UNII"),
    CAS("CAS"),
    EUROPEAN_CHEMICALS_AGENCY_EC("ECA_EC"),
    OMIM("OMIM"),
    RX_NORM_CUI("RxNorm_CUI"),
    PUB_CHEM_COMPOUND("PubChem_CID"),
    PUB_CHEM_SUBSTANCE("PubChem_SID"),
    UMLS_CUI("UMLS_CUI"),
    SNOMED_CT("SNOMED_CT"),
    MESH("MeSH"),
    NDF_RT_NUI("NDF-RT_NUI"),
    VANDF_VUID("VANDF_VUID"),
    ITIS_TAXON("ITIS_Taxon"),
    NCBI_TAXON("NCBI_Taxon"),
    NCBI_GENE("NCBI_Gene"),
    USDA_PLANTS_SYMBOL("USDA_PLANTS_Symbol"),
    ENSEMBL_GENE_ID("ENSEMBL_Gene"),
    INTERNATIONAL_NONPROPRIETARY_NAMES("INN"),
    DB_SNP("dbSNP"),
    PUBMED_ID("PMID"),
    DOI("DOI"),
    ISBN_10("ISBN10"),
    ISBN_13("ISBN13"),
    FDA_SPL("FDA_SPL"),
    PROTEIN_DATA_BANK("PDB"),
    UNIPROT_KB("UniProtKB");

    IdentifierType(final String prefix) {
        this.prefix = prefix;
    }

    public final String prefix;
}

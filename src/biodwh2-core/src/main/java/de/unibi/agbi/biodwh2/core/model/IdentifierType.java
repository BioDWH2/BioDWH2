package de.unibi.agbi.biodwh2.core.model;

public enum IdentifierType {
    CAS("CAS"),
    CHEMBL("ChEMBL"),
    CHEMSPIDER("ChemSpider"),
    DB_SNP("dbSNP"),
    DOI("DOI"),
    DRUG_BANK("DrugBank"),
    DRUG_CENTRAL("DrugCentral"),
    DUMMY("Dummy"),
    ENSEMBL_GENE_ID("ENSEMBL_Gene"),
    EUROPEAN_CHEMICALS_AGENCY_EC("ECA_EC"),
    FDA_SPL("FDA_SPL"),
    GENE_CARD("GeneCard"),
    GEN_ATLAS("GenAtlas"),
    HGNC_SYMBOL("HGNC_Symbol"),
    HGNC_ID("HGNC"),
    INTERNATIONAL_NONPROPRIETARY_NAMES("INN"),
    ISBN_10("ISBN10"),
    ISBN_13("ISBN13"),
    ITIS_TAXON("ITIS_Taxon"),
    KEGG("KEGG"),
    MESH("MeSH"),
    NCBI_TAXON("NCBI_Taxon"),
    NCBI_GENE("NCBI_Gene"),
    NDF_RT_NUI("NDF-RT_NUI"),
    OMIM("OMIM"),
    ORPHANET("ORPHA"),
    PHARM_GKB("PharmGKB"),
    PROTEIN_DATA_BANK("PDB"),
    PUB_CHEM_COMPOUND("PubChem_CID"),
    PUB_CHEM_SUBSTANCE("PubChem_SID"),
    PUBMED_ID("PMID"),
    PUBMED_CENTRAL_ID("PMCID"),
    RX_NORM_CUI("RxNorm_CUI"),
    SMPDB("SMPDB"),
    SNOMED_CT("SNOMED_CT"),
    UMLS_CUI("UMLS_CUI"),
    UNII("UNII"),
    UNIPROT_KB("UniProtKB"),
    USDA_PLANTS_SYMBOL("USDA_PLANTS_Symbol"),
    VANDF_VUID("VANDF_VUID");

    public final String prefix;

    IdentifierType(final String prefix) {
        this.prefix = prefix;
    }
}

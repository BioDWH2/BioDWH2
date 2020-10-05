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
    ITIS_TAXON("ITIS_Taxon"),
    ENSEMBL_GENE_ID("ENSEMBL_Gene");

    IdentifierType(final String prefix) {
        this.prefix = prefix;
    }

    public final String prefix;
}

package de.unibi.agbi.biodwh2.core.model;

public enum IdentifierType {
    CAS("CAS"),
    CHEMSPIDER("ChemSpider"),
    DRUG_BANK("DrugBank"),
    DRUG_CENTRAL("DrugCentral"),
    DUMMY("Dummy"),
    EUROPEAN_CHEMICALS_AGENCY_EC("ECA_EC"),
    ENSEMBL_GENE_ID("ENSEMBL_Gene"),
    GENE_CARD("GeneCard"),
    GEN_ATLAS("GenAtlas"),
    HGNC_SYMBOL("HGNC_Symbol"),
    HGNC_ID("HGNC"),
    ITIS_TAXON("ITIS_Taxon"),
    KEGG("KEGG"),
    OMIM("OMIM"),
    PFAM("Pfam"),
    PHARM_GKB("PharmGKB"),
    PUB_CHEM_COMPOUND("PubChem_CID"),
    PUB_CHEM_SUBSTANCE("PubChem_SID"),
    PUBMED("PubMed"),
    RX_NORM_CUI("RxNorm_CUI"),
    SMP_DB_ID("SmallMoleculePathwayDB"),
    UMLS_CUI("UMLS_CUI"),
    UNII("UNII"),
    UNIPROT_ACC("UniProt_Accession"),
    UNIPROTKB("UniProtKB");

    IdentifierType(final String prefix) {
        this.prefix = prefix;
    }

    public final String prefix;
}

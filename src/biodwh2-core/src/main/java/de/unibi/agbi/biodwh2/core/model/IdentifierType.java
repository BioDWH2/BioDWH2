package de.unibi.agbi.biodwh2.core.model;

public enum IdentifierType {
    /**
     * Australian Clinical Trials https://www.australianclinicaltrials.gov.au
     */
    ACTRN_TRIAL("ACTRN"),
    CAS("CAS"),
    CHEMBL("ChEMBL"),
    CHEMSPIDER("ChemSpider"),
    /**
     * Chinese Clinical Trial Registry (ChiCTR) https://www.chictr.org.cn
     */
    CHI_CTR("ChiCTR"),
    /**
     * CTRI (Clinical Trials Registry India) http://ctri.nic.in/Clinicaltrials/
     */
    CTRI_TRIAL("CTRI"),
    DB_SNP("dbSNP"),
    DOI("DOI"),
    DRUG_BANK("DrugBank"),
    DRUG_CENTRAL("DrugCentral"),
    /**
     * German Clinical Trials Register / Deutsches Register Klinischer Studien (DRKS) https://www.drks.de
     */
    DRKS_TRIAL("DRKS"),
    DUMMY("Dummy"),
    /**
     * EMA CTR (Clinical Trials Registry) https://www.clinicaltrialsregister.eu
     */
    EMA_CTR("EMA_CTR"),
    ENSEMBL_GENE_ID("ENSEMBL_Gene"),
    ENTREZ_GENE_ID("Entrez_Gene"),
    EUROPEAN_CHEMICALS_AGENCY_EC("ECA_EC"),
    FDA_SPL("FDA_SPL"),
    GENE_CARD("GeneCard"),
    GEN_ATLAS("GenAtlas"),
    HGNC_SYMBOL("HGNC_Symbol"),
    HGNC_ID("HGNC"),
    ICD10("ICD10"),
    ICD11("ICD11"),
    INTERNATIONAL_NONPROPRIETARY_NAMES("INN"),
    /**
     * Iranian Registry of Clinical Trials (IRCT) https://en.irct.ir
     */
    IRCT_TRIAL("IRCT"),
    ISBN_10("ISBN10"),
    ISBN_13("ISBN13"),
    /**
     * ISRCTN trial registry https://www.isrctn.com
     */
    ISRCTN_TRIAL("ISRCTN"),
    ITIS_TAXON("ITIS_Taxon"),
    /**
     * JPRN (Japan Primary Registries Network) https://rctportal.niph.go.jp/en/link
     */
    JPRN_TRIAL("JPRN"),
    /**
     * Korea Disease Control and Prevention Agency (KDCA) clinical trials registry https://cris.nih.go.kr
     */
    KDCA_KCT("KDCA_KCT"),
    KEGG("KEGG"),
    MESH("MeSH"),
    /**
     * Mondo Disease Ontology https://mondo.monarchinitiative.org
     */
    MONDO("MONDO"),
    NCBI_TAXON("NCBI_Taxon"),
    NCBI_GENE("NCBI_Gene"),
    /**
     * NIH NCI (National Cancer Institute) https://www.cancer.gov/about-cancer/treatment/clinical-trials
     */
    NCI_TRIAL("NCI"),
    NCT_NUMBER("NCT"),
    NDF_RT_NUI("NDF-RT_NUI"),
    /**
     * Netherlands Trial Register (NTR) https://www.trialregister.nl
     */
    NTR_TRIAL("NTR"),
    OMIM("OMIM"),
    ORPHANET("ORPHA"),
    /**
     * Pan-African Clinical Trials Registry http://www.edctp.org/pan-african-clinical-trials-registry/
     */
    PACTR_TRIAL("PACTR"),
    PANTHER("Panther"),
    PHARM_GKB("PharmGKB"),
    PROTEIN_DATA_BANK("PDB"),
    PUB_CHEM_COMPOUND("PubChem_CID"),
    PUB_CHEM_SUBSTANCE("PubChem_SID"),
    PUBMED_ID("PMID"),
    PUBMED_CENTRAL_ID("PMCID"),
    REACTOME("Reactome"),
    /**
     * Brazilian Registry of Clinical Trials (ReBEC) https://ensaiosclinicos.gov.br
     */
    REBEC_TRIAL("ReBEC"),
    /**
     * Cuban Public Registry of Clinical Trials (RPCEC) https://rpcec.sld.cu
     */
    RPCEC_TRIAL("RPCEC"),
    RX_NORM_CUI("RxNorm_CUI"),
    SMPDB("SMPDB"),
    SNOMED_CT("SNOMED_CT"),
    /**
     * Thai Clinical Trials Registry (TCTR) https://www.thaiclinicaltrials.org
     */
    TCTR_TRIAL("TCTR"),
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

package de.unibi.agbi.biodwh2.core.model;

public enum IdentifierType {
    /**
     * Australian Clinical Trials https://www.australianclinicaltrials.gov.au
     */
    ACTRN_TRIAL("ACTRN", null),
    CAS("CAS", null),
    CHEMBL("ChEMBL", null),
    CHEMSPIDER("ChemSpider", null),
    /**
     * Chinese Clinical Trial Registry (ChiCTR) https://www.chictr.org.cn
     */
    CHI_CTR("ChiCTR", null),
    /**
     * CTRI (Clinical Trials Registry India) http://ctri.nic.in/Clinicaltrials/
     */
    CTRI_TRIAL("CTRI", null),
    DB_SNP("dbSNP", CharSequence.class),
    DOI("DOI", CharSequence.class),
    DRUG_BANK("DrugBank", CharSequence.class),
    DRUG_CENTRAL("DrugCentral", Integer.class),
    /**
     * German Clinical Trials Register / Deutsches Register Klinischer Studien (DRKS) https://www.drks.de
     */
    DRKS_TRIAL("DRKS", null),
    DUMMY("Dummy", null),
    EFO("EFO", null),
    /**
     * EMA CTR (Clinical Trials Registry) https://www.clinicaltrialsregister.eu
     */
    EMA_CTR("EMA_CTR", null),
    ENSEMBL_GENE_ID("ENSEMBL_Gene", CharSequence.class),
    ENTREZ_GENE_ID("Entrez_Gene", Integer.class),
    EUROPEAN_CHEMICALS_AGENCY_EC("ECA_EC", null),
    FDA_SPL("FDA_SPL", null),
    GENE_CARD("GeneCard", null),
    GEN_ATLAS("GenAtlas", null),
    HGNC_SYMBOL("HGNC_Symbol", CharSequence.class),
    HGNC_ID("HGNC", Integer.class),
    ICD10("ICD10", null),
    ICD11("ICD11", null),
    INTERNATIONAL_NONPROPRIETARY_NAMES("INN", null),
    /**
     * Iranian Registry of Clinical Trials (IRCT) https://en.irct.ir
     */
    IRCT_TRIAL("IRCT", null),
    ISBN_10("ISBN10", null),
    ISBN_13("ISBN13", null),
    /**
     * ISRCTN trial registry https://www.isrctn.com
     */
    ISRCTN_TRIAL("ISRCTN", null),
    ITIS_TAXON("ITIS_Taxon", null),
    /**
     * JPRN (Japan Primary Registries Network) https://rctportal.niph.go.jp/en/link
     */
    JPRN_TRIAL("JPRN", null),
    /**
     * Korea Disease Control and Prevention Agency (KDCA) clinical trials registry https://cris.nih.go.kr
     */
    KDCA_KCT("KDCA_KCT", null),
    KEGG("KEGG", null),
    MEDDRA("MEDDRA", null),
    MESH("MeSH", null),
    MIRBASE("MIRBASE", null),
    MIRNA("miRNA", null),
    /**
     * Mondo Disease Ontology https://mondo.monarchinitiative.org
     */
    MONDO("MONDO", null),
    NCBI_TAXON("NCBI_Taxon", null),
    NCBI_GENE("NCBI_Gene", null),
    /**
     * NIH NCI (National Cancer Institute) https://www.cancer.gov/about-cancer/treatment/clinical-trials
     */
    NCI_TRIAL("NCI", null),
    /**
     * ClinicalTrials.gov http://clinicaltrials.gov
     */
    NCT_NUMBER("NCT", null),
    NDF_RT_NUI("NDF-RT_NUI", null),
    /**
     * Netherlands Trial Register (NTR) https://www.trialregister.nl
     */
    NTR_TRIAL("NTR", null),
    OMIM("OMIM", Integer.class),
    ORPHANET("ORPHA", null),
    /**
     * Pan-African Clinical Trials Registry http://www.edctp.org/pan-african-clinical-trials-registry/
     */
    PACTR_TRIAL("PACTR", null),
    PANTHER("Panther", null),
    PFAM("Pfam", null),
    PHARM_GKB("PharmGKB", null),
    PROTEIN_DATA_BANK("PDB", null),
    PUB_CHEM_COMPOUND("PubChem_CID", null),
    PUB_CHEM_SUBSTANCE("PubChem_SID", null),
    PUBMED_ID("PMID", Integer.class),
    PUBMED_CENTRAL_ID("PMCID", CharSequence.class),
    REACTOME("Reactome", null),
    /**
     * Brazilian Registry of Clinical Trials (ReBEC) https://ensaiosclinicos.gov.br
     */
    REBEC_TRIAL("ReBEC", null),
    /**
     * Cuban Public Registry of Clinical Trials (RPCEC) https://rpcec.sld.cu
     */
    RPCEC_TRIAL("RPCEC", null),
    RX_NORM_CUI("RxNorm_CUI", null),
    SMPDB("SMPDB", null),
    SNOMED_CT("SNOMED_CT", null),
    /**
     * Thai Clinical Trials Registry (TCTR) https://www.thaiclinicaltrials.org
     */
    TCTR_TRIAL("TCTR", null),
    UMLS_CUI("UMLS_CUI", null),
    UNII("UNII", null),
    UNIPROT_KB("UniProtKB", null),
    USDA_PLANTS_SYMBOL("USDA_PLANTS_Symbol", null),
    VANDF_VUID("VANDF_VUID", null),
    WORM_BASE("WORM_BASE", null);

    public final String prefix;
    public final Class<?> expectedType;

    IdentifierType(final String prefix, final Class<?> expectedType) {
        this.prefix = prefix;
        this.expectedType = expectedType;
    }
}

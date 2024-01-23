package de.unibi.agbi.biodwh2.core.model;

@SuppressWarnings("JavadocLinkAsPlainText")
public enum IdentifierType {
    /**
     * Australian Clinical Trials https://www.australianclinicaltrials.gov.au
     */
    ACTRN_TRIAL("ACTRN", null, null, null),
    CAS("CAS", CharSequence.class, "cas", "^\\d{1,7}-\\d{2}-\\d$"),
    CHEMBL("ChEMBL", null, null, null),
    CHEMSPIDER("ChemSpider", null, null, null),
    /**
     * Chinese Clinical Trial Registry (ChiCTR) https://www.chictr.org.cn
     */
    CHI_CTR("ChiCTR", null, null, null),
    /**
     * CTRI (Clinical Trials Registry India) http://ctri.nic.in/Clinicaltrials/
     */
    CTRI_TRIAL("CTRI", null, null, null),
    DB_SNP("dbSNP", CharSequence.class, null, null),
    /**
     * Digital Object Identifier (DOI) https://www.doi.org
     */
    DOI("DOI", CharSequence.class, "doi", "^10.\\d{2,9}/.*$"),
    DRUG_BANK("DrugBank", CharSequence.class, null, null),
    DRUG_CENTRAL("DrugCentral", Integer.class, null, null),
    /**
     * German Clinical Trials Register / Deutsches Register Klinischer Studien (DRKS) https://www.drks.de
     */
    DRKS_TRIAL("DRKS", null, null, null),
    DUMMY("Dummy", null, null, null),
    EFO("EFO", null, null, null),
    /**
     * EMA CTR (Clinical Trials Registry) https://www.clinicaltrialsregister.eu
     */
    EMA_CTR("EMA_CTR", null, null, null),
    ENSEMBL("ENSEMBL", CharSequence.class, null, null),
    /**
     * Commission of the European Communities EC number for commercially available chemical substances within the
     * European Union. The European EC Number should not be confused with the Enzyme Commission EC number for enzymes.
     */
    EUROPEAN_CHEMICALS_AGENCY_EC("ECA_EC", null, null, null),
    /**
     * Enzyme Commission number (EC number)
     */
    EC_NUMBER("EC", null, null, null),
    FDA_SPL("FDA_SPL", null, null, null),
    GENE_CARD("GeneCard", null, null, null),
    GEN_ATLAS("GenAtlas", null, null, null),
    HGNC_SYMBOL("HGNC_Symbol", CharSequence.class, "hgnc.symbol", "^[A-Za-z-0-9_]+(\\@)?$"),
    HGNC_ID("HGNC", Integer.class, "hgnc", "^\\d{1,5}$"),
    ICD10("ICD10", null, null, null),
    ICD11("ICD11", null, null, null),
    /**
     * International Nonproprietary Names (INN)
     */
    INN("INN", null, "inn", null),
    /**
     * Iranian Registry of Clinical Trials (IRCT) https://en.irct.ir
     */
    IRCT_TRIAL("IRCT", null, null, null),
    ISBN_10("ISBN10", null, null, null),
    ISBN_13("ISBN13", null, null, null),
    /**
     * ISRCTN trial registry https://www.isrctn.com
     */
    ISRCTN_TRIAL("ISRCTN", null, null, null),
    /**
     * Integrated Taxonomic Information System (ITIS) https://www.itis.gov
     */
    ITIS("ITIS", Integer.class, "itis", "^\\d+$"),
    /**
     * JPRN (Japan Primary Registries Network) https://rctportal.niph.go.jp/en/link
     */
    JPRN_TRIAL("JPRN", null, null, null),
    /**
     * Korea Disease Control and Prevention Agency (KDCA) clinical trials registry https://cris.nih.go.kr
     */
    KDCA_KCT("KDCA_KCT", null, null, null),
    KEGG("KEGG", null, null, null),
    MEDDRA("MEDDRA", null, null, null),
    MESH("MeSH", null, null, null),
    MIRBASE("MIRBASE", null, null, null),
    MIRNA("miRNA", null, null, null),
    /**
     * Mondo Disease Ontology https://mondo.monarchinitiative.org
     */
    MONDO("MONDO", null, null, null),
    NCBI_TAXON("NCBITaxon", Integer.class, "ncbitaxon", "^\\d+$"),
    /**
     * NCBI Entrez Gene ID
     */
    NCBI_GENE("NCBIGene", Integer.class, "ncbigene", "^\\d+$"),
    GENBANK("Genbank", CharSequence.class, null, null),
    /**
     * NIH NCI (National Cancer Institute) https://www.cancer.gov/about-cancer/treatment/clinical-trials
     */
    NCI_TRIAL("NCI", null, null, null),
    /**
     * ClinicalTrials.gov http://clinicaltrials.gov
     */
    NCT_NUMBER("NCT", null, null, null),
    NDF_RT_NUI("NDF-RT_NUI", null, null, null),
    /**
     * Netherlands Trial Register (NTR) https://www.trialregister.nl
     */
    NTR_TRIAL("NTR", null, null, null),
    OMIM("OMIM", Integer.class, null, null),
    ORPHANET("ORPHA", null, null, null),
    /**
     * Pan-African Clinical Trials Registry http://www.edctp.org/pan-african-clinical-trials-registry/
     */
    PACTR_TRIAL("PACTR", null, null, null),
    PANTHER("Panther", null, null, null),
    PFAM("Pfam", null, null, null),
    PHARM_GKB("PharmGKB", null, null, null),
    PROTEIN_DATA_BANK("PDB", null, null, null),
    PUB_CHEM_COMPOUND("PubChem_CID", Integer.class, "pubchem.compound", "^\\d+$"),
    PUB_CHEM_SUBSTANCE("PubChem_SID", Integer.class, "pubchem.substance", "^\\d+$"),
    PUBMED_ID("PMID", Integer.class, "pubmed", "^\\d+$"),
    PUBMED_CENTRAL_ID("PMCID", CharSequence.class, "pmc", "^PMC\\d+$"),
    REACTOME("Reactome", null, null, null),
    /**
     * Brazilian Registry of Clinical Trials (ReBEC) https://ensaiosclinicos.gov.br
     */
    REBEC_TRIAL("ReBEC", null, null, null),
    /**
     * Cuban Public Registry of Clinical Trials (RPCEC) https://rpcec.sld.cu
     */
    RPCEC_TRIAL("RPCEC", null, null, null),
    RX_NORM_CUI("RxNorm_CUI", null, null, null),
    SMPDB("SMPDB", null, null, null),
    SNOMED_CT("SNOMED_CT", null, null, null),
    /**
     * Thai Clinical Trials Registry (TCTR) https://www.thaiclinicaltrials.org
     */
    TCTR_TRIAL("TCTR", null, null, null),
    UMLS_CUI("UMLS_CUI", null, null, null),
    UNII("UNII", null, null, null),
    UNIPROT_KB("UniProtKB", null, null, null),
    USDA_PLANTS_SYMBOL("USDA_PLANTS_Symbol", null, null, null),
    VANDF_VUID("VANDF_VUID", null, null, null),
    WORM_BASE("WORM_BASE", null, null, null);

    public final String prefix;
    public final Class<?> expectedType;
    public final String bioregistryId;
    public final String localPattern;

    IdentifierType(final String prefix, final Class<?> expectedType, final String bioregistryId,
                   final String localPattern) {
        this.prefix = prefix;
        this.expectedType = expectedType;
        this.bioregistryId = bioregistryId;
        this.localPattern = localPattern;
    }
}

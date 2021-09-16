package de.unibi.agbi.biodwh2.gwascatalog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "DATE ADDED TO CATALOG", "PUBMEDID", "FIRST AUTHOR", "DATE", "JOURNAL", "LINK", "STUDY", "DISEASE/TRAIT",
        "INITIAL SAMPLE SIZE", "REPLICATION SAMPLE SIZE", "REGION", "CHR_ID", "CHR_POS", "REPORTED GENE(S)",
        "MAPPED_GENE", "UPSTREAM_GENE_ID", "DOWNSTREAM_GENE_ID", "SNP_GENE_IDS", "UPSTREAM_GENE_DISTANCE",
        "DOWNSTREAM_GENE_DISTANCE", "STRONGEST SNP-RISK ALLELE", "SNPS", "MERGED", "SNP_ID_CURRENT", "CONTEXT",
        "INTERGENIC", "RISK ALLELE FREQUENCY", "P-VALUE", "PVALUE_MLOG", "P-VALUE (TEXT)", "OR or BETA",
        "95% CI (TEXT)", "PLATFORM [SNPS PASSING QC]", "CNV", "MAPPED_TRAIT", "MAPPED_TRAIT_URI", "STUDY ACCESSION",
        "GENOTYPING TECHNOLOGY"
})
public final class Association {
    @JsonProperty("DATE ADDED TO CATALOG")
    public String dateAddedToCatalog;
    @JsonProperty("PUBMEDID")
    public String pubmedId;
    @JsonProperty("FIRST AUTHOR")
    public String firstAuthor;
    @JsonProperty("DATE")
    public String datePublished;
    @JsonProperty("JOURNAL")
    public String journal;
    @JsonProperty("LINK")
    public String link;
    @JsonProperty("STUDY")
    public String studyTitle;
    @JsonProperty("DISEASE/TRAIT")
    public String diseaseOrTrait;
    @JsonProperty("INITIAL SAMPLE SIZE")
    public String initialSampleSize;
    @JsonProperty("REPLICATION SAMPLE SIZE")
    public String replicationSampleSize;
    @JsonProperty("REGION")
    public String region;
    @JsonProperty("CHR_ID")
    public String chrId;
    @JsonProperty("CHR_POS")
    public String chrPosition;
    @JsonProperty("REPORTED GENE(S)")
    public String reportedGenes;
    @JsonProperty("MAPPED_GENE")
    public String mappedGenes;
    @JsonProperty("UPSTREAM_GENE_ID")
    public String upstreamGeneId;
    @JsonProperty("DOWNSTREAM_GENE_ID")
    public String downstreamGeneId;
    @JsonProperty("SNP_GENE_IDS")
    public String snpGeneIds;
    @JsonProperty("UPSTREAM_GENE_DISTANCE")
    public String upstreamGeneDistance;
    @JsonProperty("DOWNSTREAM_GENE_DISTANCE")
    public String downstreamGeneDistance;
    @JsonProperty("STRONGEST SNP-RISK ALLELE")
    public String strongestSNPRiskAllele;
    @JsonProperty("SNPS")
    public String snps;
    @JsonProperty("MERGED")
    public String merged;
    @JsonProperty("SNP_ID_CURRENT")
    public String snpIdCurrent;
    @JsonProperty("CONTEXT")
    public String context;
    @JsonProperty("INTERGENIC")
    public String intergenic;
    @JsonProperty("RISK ALLELE FREQUENCY")
    public String riskAlleleFrequency;
    @JsonProperty("P-VALUE")
    public String pValue;
    @JsonProperty("PVALUE_MLOG")
    public String pValueMlog;
    @JsonProperty("P-VALUE (TEXT)")
    public String pValueText;
    @JsonProperty("OR or BETA")
    public String orOrBeta;
    @JsonProperty("95% CI (TEXT)")
    public String ninetyfiveConfidenceIntervalText;
    @JsonProperty("PLATFORM [SNPS PASSING QC]")
    public String platform;
    @JsonProperty("CNV")
    public String cnv;
    @JsonProperty("MAPPED_TRAIT")
    public String mappedTrait;
    @JsonProperty("MAPPED_TRAIT_URI")
    public String mappedTraitUri;
    @JsonProperty("STUDY ACCESSION")
    public String studyAccession;
    @JsonProperty("GENOTYPING TECHNOLOGY")
    public String genotypingTechnology;
}

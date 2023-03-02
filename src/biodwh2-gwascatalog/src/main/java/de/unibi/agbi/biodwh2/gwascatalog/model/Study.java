package de.unibi.agbi.biodwh2.gwascatalog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "DATE ADDED TO CATALOG", "PUBMEDID", "FIRST AUTHOR", "DATE", "JOURNAL", "LINK", "STUDY", "DISEASE/TRAIT",
        "INITIAL SAMPLE SIZE", "REPLICATION SAMPLE SIZE", "PLATFORM [SNPS PASSING QC]", "ASSOCIATION COUNT",
        "MAPPED_TRAIT", "MAPPED_TRAIT_URI", "STUDY ACCESSION", "GENOTYPING TECHNOLOGY"
})
public final class Study {
    @JsonProperty("DATE ADDED TO CATALOG")
    public String dateAddedToCatalog;
    @JsonProperty("PUBMEDID")
    public Integer pubmedId;
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
    @JsonProperty("PLATFORM [SNPS PASSING QC]")
    public String platform;
    @JsonProperty("ASSOCIATION COUNT")
    public String associationCount;
    @JsonProperty("MAPPED_TRAIT")
    public String mappedTrait;
    @JsonProperty("MAPPED_TRAIT_URI")
    public String mappedTraitUri;
    @JsonProperty("STUDY ACCESSION")
    public String studyAccession;
    @JsonProperty("GENOTYPING TECHNOLOGY")
    public String genotypingTechnology;
}

package de.unibi.agbi.biodwh2.gwascatalog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "STUDY ACCESSION", "PUBMEDID", "FIRST AUTHOR", "DATE", "INITIAL SAMPLE DESCRIPTION",
        "REPLICATION SAMPLE DESCRIPTION", "STAGE", "NUMBER OF INDIVDUALS", "BROAD ANCESTRAL CATEGORY",
        "COUNTRY OF ORIGIN", "COUNTRY OF RECRUITMENT", "ADDITONAL ANCESTRY DESCRIPTION"
})
public final class Ancestry {
    @JsonProperty("STUDY ACCESSION")
    public String studyAccession;
    @JsonProperty("PUBMEDID")
    public String pubmedId;
    @JsonProperty("FIRST AUTHOR")
    public String firstAuthor;
    @JsonProperty("DATE")
    public String datePublished;
    @JsonProperty("INITIAL SAMPLE DESCRIPTION")
    public String initialSampleDescription;
    @JsonProperty("REPLICATION SAMPLE DESCRIPTION")
    public String replicationSampleDescription;
    @JsonProperty("STAGE")
    public String stage;
    @JsonProperty("NUMBER OF INDIVDUALS")
    public String numberOfIndividuals;
    @JsonProperty("BROAD ANCESTRAL CATEGORY")
    public String broadAncestralCategory;
    @JsonProperty("COUNTRY OF ORIGIN")
    public String countryOfOrigin;
    @JsonProperty("COUNTRY OF RECRUITMENT")
    public String countryOfRecruitment;
    @JsonProperty("ADDITONAL ANCESTRY DESCRIPTION")
    public String additionalAncestryDescription;
}

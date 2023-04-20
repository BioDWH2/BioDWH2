package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Clinical Annotation ID", "Evidence ID", "Evidence Type", "Evidence URL", "PMID", "Summary", "Score"
})
public class ClinicalAnnotationEvidence {
    @JsonProperty("Clinical Annotation ID")
    public Integer clinicalAnnotationId;
    @JsonProperty("Evidence ID")
    public String evidenceId;
    @JsonProperty("Evidence Type")
    public String evidenceType;
    @JsonProperty("Evidence URL")
    public String evidenceUrl;
    @JsonProperty("PMID")
    public String pmid;
    @JsonProperty("Summary")
    public String summary;
    @JsonProperty("Score")
    public String score;
}

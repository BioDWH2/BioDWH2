package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Clinical Annotation ID", "Date (YYYY-MM-DD)", "Type", "Comment"
})
public class ClinicalAnnotationHistory {
    @JsonProperty("Clinical Annotation ID")
    public Integer clinicalAnnotationId;
    @JsonProperty("Date (YYYY-MM-DD)")
    public String date;
    @JsonProperty("Type")
    public String type;
    @JsonProperty("Comment")
    public String comment;
}

package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"clinical_trial_id", "accession", "title", "url", "type", "description", "source"})
@GraphNodeLabel("ClinicalTrial")
public class ClinicalTrial {
    @JsonProperty("clinical_trial_id")
    @GraphProperty("id")
    public Long clinicalTrialId;
    @JsonProperty("accession")
    @GraphProperty("nct_id")
    public String accession;
    @JsonProperty("title")
    @GraphProperty("title")
    public String title;
    @JsonProperty("url")
    @GraphProperty("url")
    public String url;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
}

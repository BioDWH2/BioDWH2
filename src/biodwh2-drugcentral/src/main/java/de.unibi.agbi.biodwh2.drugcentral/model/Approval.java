package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "struct_id", "approval", "type", "applicant", "orphan"})
@NodeLabels({"Approval"})
public final class Approval {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public Integer structId;
    @JsonProperty("approval")
    @GraphProperty("approval")
    public String approval;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("applicant")
    @GraphProperty("applicant")
    public String applicant;
    @JsonProperty("orphan")
    @GraphProperty("orphan")
    public String orphan;
}

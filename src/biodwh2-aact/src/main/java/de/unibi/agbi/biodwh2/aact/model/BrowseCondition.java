package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "mesh_term", "downcase_mesh_term"})
public class BrowseCondition {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("mesh_term")
    public String meshTerm;
    @JsonProperty("downcaseMeshTerm")
    public String downcaseMeshTerm;
}

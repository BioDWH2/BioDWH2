package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"expert_id_prefix", "expert_id", "expert", "exp_comment", "update_date"})
@GraphNodeLabel("Expert")
public class Expert {
    @JsonProperty("expert_id_prefix")
    public String idPrefix;
    @JsonProperty("expert_id")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("expert")
    @GraphProperty("name")
    public String name;
    @JsonProperty("exp_comment")
    @GraphProperty("comment")
    public String comment;
    @JsonProperty("update_date")
    public String updateDate;
}

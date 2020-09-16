package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"expert_id_prefix", "expert_id", "expert", "exp_comment", "update_date"})
public class Expert {
    @JsonProperty("expert_id_prefix")
    public String idPrefix;
    @JsonProperty("expert_id")
    public int id;
    @JsonProperty("expert")
    public String expert;
    @JsonProperty("exp_comment")
    public String expertComment;
    @JsonProperty("update_date")
    public String updateDate;
}

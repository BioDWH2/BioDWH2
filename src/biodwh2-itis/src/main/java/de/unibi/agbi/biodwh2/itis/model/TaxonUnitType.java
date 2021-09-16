package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"kingdom_id", "rank_id", "rank_name", "dir_parent_rank_id", "req_parent_rank_id", "update_date"})
public class TaxonUnitType {
    @JsonProperty("kingdom_id")
    public int kingdomId;
    @JsonProperty("rank_id")
    public int id;
    @JsonProperty("rank_name")
    public String name;
    @JsonProperty("dir_parent_rank_id")
    public int dirParentRankId;
    @JsonProperty("req_parent_rank_id")
    public int reqParentRankId;
    @JsonProperty("update_date")
    public String updateDate;
}

package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"kingdom_id", "rank_id", "rank_name", "dir_parent_rank_id", "req_parent_rank_id", "update_date"})
public class TaxonUnitType {
    @JsonProperty("kingdom_id")
    public int kingdomId;
    @JsonProperty("rank_id")
    public int rankId;
    @JsonProperty("rank_name")
    public String rankName;
    @JsonProperty("dir_parent_rank_id")
    public int dirParentRankId;
    @JsonProperty("req_parent_rank_id")
    public int reqParentRankId;
    @JsonProperty("update_date")
    public String updateDate;
}

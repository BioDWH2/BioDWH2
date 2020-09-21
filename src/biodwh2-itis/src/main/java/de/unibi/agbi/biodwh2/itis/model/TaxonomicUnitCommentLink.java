package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"tsn", "comment_id", "update_date"})
public class TaxonomicUnitCommentLink {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("comment_id")
    public int commentId;
    @JsonProperty("update_date")
    public String updateDate;
}

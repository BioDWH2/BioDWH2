package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"comment_id", "commentator", "comment_detail", "comment_time_stamp", "update_date"})
@NodeLabel("Comment")
public class Comment {
    @JsonProperty("comment_id")
    @GraphProperty("id")
    public int id;
    @JsonProperty("commentator")
    @GraphProperty("commentator")
    public String commentator;
    @JsonProperty("comment_detail")
    @GraphProperty("detail")
    public String detail;
    @JsonProperty("comment_time_stamp")
    @GraphProperty("timestamp")
    public String timeStamp;
    @JsonProperty("update_date")
    public String updateDate;
}

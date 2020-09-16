package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"comment_id", "commentator", "comment_detail", "comment_time_stamp", "update_date"})
public class Comment {
    @JsonProperty("comment_id")
    public int id;
    @JsonProperty("commentator")
    public String commentator;
    @JsonProperty("comment_detail")
    public String detail;
    @JsonProperty("comment_time_stamp")
    public String timeStamp;
    @JsonProperty("update_date")
    public String updateDate;
}

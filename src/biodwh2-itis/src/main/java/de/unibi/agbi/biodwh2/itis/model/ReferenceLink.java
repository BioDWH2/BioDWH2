package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "tsn", "doc_id_prefix", "documentation_id", "original_desc_ind", "init_itis_desc_ind", "change_track_id",
        "vernacular_name", "update_date"
})
public class ReferenceLink {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("doc_id_prefix")
    public String docIdPrefix;
    @JsonProperty("documentation_id")
    public int documentationId;
    @JsonProperty("original_desc_ind")
    public String originalDescInd;
    @JsonProperty("init_itis_desc_ind")
    public String initItisDescInd;
    @JsonProperty("change_track_id")
    public int changeTrackId;
    @JsonProperty("vernacular_name")
    public String vernacularName;
    @JsonProperty("update_date")
    public String updateDate;
}

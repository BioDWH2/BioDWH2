package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "tsn", "doc_id_prefix", "documentation_id", "original_desc_ind", "init_itis_desc_ind", "change_track_id",
        "vernacular_name", "update_date"
})
public class ReferenceLink {
    @JsonProperty("tsn")
    public Integer tsn;
    @JsonProperty("doc_id_prefix")
    public String docIdPrefix;
    @JsonProperty("documentation_id")
    public Integer documentationId;
    /**
     * Indicator used to identify that this occurrence represents the reference of the original description, when
     * available.
     */
    @JsonProperty("original_desc_ind")
    public String originalDescInd;
    /**
     * Indicator used to identify the reference(s) that serve as the reason for an occurrence of Taxonomic Units being
     * recognized where the original reference is unavailable.
     */
    @JsonProperty("init_itis_desc_ind")
    public String initItisDescInd;
    /**
     * The unique identifier assigned to a change made to an occurrence of Taxonomic Units.
     */
    @JsonProperty("change_track_id")
    public Integer changeTrackId;
    /**
     * A common name associated with an occurrence of Taxonomic Units.
     */
    @JsonProperty("vernacular_name")
    public String vernacularName;
    @JsonProperty("update_date")
    public String updateDate;
}

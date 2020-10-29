package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"tsn", "doc_id_prefix", "documentation_id", "update_date", "vern_id"})
public class VernacularReferenceLink {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("doc_id_prefix")
    public String docIdPrefix;
    @JsonProperty("documentation_id")
    public int documentationId;
    @JsonProperty("update_date")
    public String updateDate;
    @JsonProperty("vern_id")
    public int vernacularId;
}

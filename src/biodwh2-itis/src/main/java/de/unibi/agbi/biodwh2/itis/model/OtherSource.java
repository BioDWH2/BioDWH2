package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "source_id_prefix", "source_id", "source_type", "source", "version", "acquisition_date", "source_comment",
        "update_date"
})
public class OtherSource {
    @JsonProperty("source_id_prefix")
    public String idPrefix;
    @JsonProperty("source_id")
    public int id;
    @JsonProperty("source_type")
    public String type;
    @JsonProperty("source")
    public String source;
    @JsonProperty("version")
    public String version;
    @JsonProperty("acquisition_date")
    public String acquisitionDate;
    @JsonProperty("source_comment")
    public String comment;
    @JsonProperty("update_date")
    public String updateDate;
}

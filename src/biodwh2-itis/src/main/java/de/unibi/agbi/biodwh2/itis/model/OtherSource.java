package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "source_id_prefix", "source_id", "source_type", "source", "version", "acquisition_date", "source_comment",
        "update_date"
})
@NodeLabel("Source")
public class OtherSource {
    @JsonProperty("source_id_prefix")
    public String idPrefix;
    @JsonProperty("source_id")
    @GraphProperty("id")
    public int id;
    @JsonProperty("source_type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
    @JsonProperty("version")
    @GraphProperty("version")
    public String version;
    @JsonProperty("acquisition_date")
    public String acquisitionDate;
    @JsonProperty("source_comment")
    @GraphProperty("comment")
    public String comment;
    @JsonProperty("update_date")
    public String updateDate;
}

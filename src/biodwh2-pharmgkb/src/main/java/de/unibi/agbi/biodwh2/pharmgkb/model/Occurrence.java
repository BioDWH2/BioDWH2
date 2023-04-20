package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Source Type", "Source ID", "Source Name", "Object Type", "Object ID", "Object Name"
})
public class Occurrence {
    @JsonProperty("Source Type")
    public String sourceType;
    @JsonProperty("Source ID")
    public String sourceId;
    @JsonProperty("Source Name")
    public String sourceName;
    @JsonProperty("Object Type")
    public String objectType;
    @JsonProperty("Object ID")
    public String objectId;
    @JsonProperty("Object Name")
    public String objectName;
}
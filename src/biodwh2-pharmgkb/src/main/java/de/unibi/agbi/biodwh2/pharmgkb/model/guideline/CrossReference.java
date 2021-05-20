package de.unibi.agbi.biodwh2.pharmgkb.model.guideline;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CrossReference {
    public Integer id;
    public String resource;
    public String resourceId;
    @JsonProperty("_url")
    public String url;
    public Integer version;
}

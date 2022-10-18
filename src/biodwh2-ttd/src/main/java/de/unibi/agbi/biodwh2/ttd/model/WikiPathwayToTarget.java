package de.unibi.agbi.biodwh2.ttd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"TTDID", "Wiki pathway ID", "Wiki pathway name"})
public class WikiPathwayToTarget {
    @JsonProperty("TTDID")
    public String ttdId;
    @JsonProperty("Wiki pathway ID")
    public String wikiPathwayId;
    @JsonProperty("Wiki pathway name")
    public String wikiPathwayName;

}

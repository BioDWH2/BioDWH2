package de.unibi.agbi.biodwh2.ttd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"TTDID", "KEGG pathway ID", "KEGG pathway name"})
public class KEGGPathwayToTarget {
    @JsonProperty("TTDID")
    public String ttdId;
    @JsonProperty("KEGG pathway ID")
    public String keggPathwayId;
    @JsonProperty("KEGG pathway name")
    public String keggPathwayName;
}

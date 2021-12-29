package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#taxon_id", "STRING_type", "STRING_name_compact", "official_name_NCBI", "domain"})
public class Species {
    @JsonProperty("#taxon_id")
    public Integer taxonId;
    @JsonProperty("STRING_type")
    public String type;
    @JsonProperty("STRING_name_compact")
    public String nameCompact;
    @JsonProperty("official_name_NCBI")
    public String officialNameNCBI;
    @JsonProperty("domain")
    public String domain;
}

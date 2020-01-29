package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "stem", "definition", "national_name", "length", "discontinued"})

public final class InnStem {
    @JsonProperty("id")
    public String id;
    @JsonProperty("stem")
    public String stem;
    @JsonProperty("definition")
    public String definition;
    @JsonProperty("national_name")
    public String nationalName;
    @JsonProperty("length")
    public String length;
    @JsonProperty("discontinued")
    public String discontinued;
}

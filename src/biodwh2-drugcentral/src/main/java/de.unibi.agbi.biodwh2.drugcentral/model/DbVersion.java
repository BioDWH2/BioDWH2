package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"version", "dtime"})

public final class DbVersion {
    @JsonProperty("version")
    public String version;
    @JsonProperty("dtime")
    public String dtime;
}

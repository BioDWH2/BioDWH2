package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Brenda {
    @JsonProperty("release")
    public String release;
    @JsonProperty("version")
    public String version;
    @JsonProperty("data")
    public Map<String, Enzyme> data;
}

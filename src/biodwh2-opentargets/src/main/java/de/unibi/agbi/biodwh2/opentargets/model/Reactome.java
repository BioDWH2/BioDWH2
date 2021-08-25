package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reactome {
    @JsonProperty("id")
    public String id;
    @JsonProperty("label")
    public String label;
    @JsonProperty("ancestors")
    public String[] ancestors;
    @JsonProperty("descendants")
    public String[] descendants;
    @JsonProperty("children")
    public String[] children;
    @JsonProperty("parents")
    public String[] parents;
    @JsonProperty("path")
    public String[][] path;
}

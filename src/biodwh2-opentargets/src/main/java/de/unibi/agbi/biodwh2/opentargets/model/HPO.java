package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HPO {
    @JsonProperty("id")
    public String id;
    @JsonProperty("description")
    public String description;
    @JsonProperty("name")
    public String name;
    @JsonProperty("namespace")
    public String[] namespace;
    @JsonProperty("dbXRefs")
    public String[] dbXRefs;
    @JsonProperty("obsolete_terms")
    public String[] obsoleteTerms;
    @JsonProperty("parents")
    public String[] parents;
}

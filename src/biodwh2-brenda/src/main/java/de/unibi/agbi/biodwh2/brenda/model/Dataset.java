package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dataset {
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("proteins")
    public String[] proteins;
    @JsonProperty("references")
    public String[] references;
}

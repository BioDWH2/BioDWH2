package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Textbook {
    @JsonProperty("ref-id")
    public String refId;
    public String isbn;
    public String citation;
}

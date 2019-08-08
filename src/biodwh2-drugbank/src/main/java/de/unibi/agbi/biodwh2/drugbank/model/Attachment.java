package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Attachment {
    @JsonProperty("ref-id")
    public String refId;
    public String title;
    public String url;
}

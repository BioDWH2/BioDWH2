package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkStruct {
    @JsonProperty(required = true)
    public String url;
    public String description;
}

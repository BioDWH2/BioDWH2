package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Url {
    @JsonProperty("niceName")
    public String niceName;
    @JsonProperty("url")
    public String url;
}

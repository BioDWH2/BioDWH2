package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Patent {
    public String number;
    public String country;
    public String approved;
    public String expires;
    @JsonProperty("pediatric-extension")
    public boolean pediatricExtension;
}

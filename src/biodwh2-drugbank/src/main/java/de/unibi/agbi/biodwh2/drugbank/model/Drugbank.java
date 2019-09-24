package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

@JsonIgnoreProperties(value = {"schemaLocation"})
public final class Drugbank {
    public String version;
    @JsonProperty("exported-on")
    public String exportedOn;
    @JsonProperty("drug")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<Drug> drugs;
}

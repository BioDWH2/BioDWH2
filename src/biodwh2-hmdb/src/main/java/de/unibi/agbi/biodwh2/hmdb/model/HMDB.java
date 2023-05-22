package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public final class HMDB {
    @JsonProperty("metabolite")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<Metabolite> metabolites;
}

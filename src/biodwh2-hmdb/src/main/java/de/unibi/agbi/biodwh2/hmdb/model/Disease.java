package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Disease {
    public String name;
    @JsonProperty("omim_id")
    public Integer omimId;
    @JacksonXmlElementWrapper(localName = "references")
    public List<Reference> references;
}

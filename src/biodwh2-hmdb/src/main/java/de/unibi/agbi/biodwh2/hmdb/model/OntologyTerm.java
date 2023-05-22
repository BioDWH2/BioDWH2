package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class OntologyTerm {
    public String term;
    public String definition;
    @JsonProperty("parent_id")
    public Integer parentId;
    public Integer level;
    public String type;
    @JacksonXmlElementWrapper(localName = "synonyms")
    public List<String> synonyms;
    @JacksonXmlElementWrapper(localName = "descendants")
    public List<OntologyTerm> descendants;
}

package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class BrowseStruct {
    @JsonProperty(value = "mesh_term")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> meshTerm;
}

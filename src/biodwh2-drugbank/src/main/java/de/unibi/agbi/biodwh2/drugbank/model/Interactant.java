package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

public abstract class Interactant {
    @JacksonXmlProperty(isAttribute = true)
    public Integer position;
    public String id;
    public String name;
    public String organism;
    public ArrayList<String> actions;
    public ReferenceList references;
    @JsonProperty("known-action")
    public KnownAction knownAction;
    public Polypeptide polypeptide;
}

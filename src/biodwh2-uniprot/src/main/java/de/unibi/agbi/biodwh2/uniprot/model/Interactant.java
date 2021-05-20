package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Interactant {
    public String id;
    public String label;
    public DbReference dbReference;
    @JacksonXmlProperty(isAttribute = true)
    public String intactId;
}

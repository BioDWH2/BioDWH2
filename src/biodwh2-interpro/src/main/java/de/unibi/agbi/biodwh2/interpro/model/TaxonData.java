package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TaxonData {
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    public String name;
    @JacksonXmlProperty(localName = "proteins_count", isAttribute = true)
    public Integer proteinsCount;
}

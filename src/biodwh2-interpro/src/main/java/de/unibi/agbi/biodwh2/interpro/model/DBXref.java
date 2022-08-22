package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DBXref {
    @JacksonXmlProperty(isAttribute = true)
    public String db;
    @JacksonXmlProperty(localName = "dbkey", isAttribute = true)
    public String dbKey;
    @JacksonXmlProperty(isAttribute = true)
    public String version;
    @JacksonXmlProperty(isAttribute = true)
    public String name;
    @JacksonXmlProperty(localName = "protein_count", isAttribute = true)
    public Integer proteinCount;
}

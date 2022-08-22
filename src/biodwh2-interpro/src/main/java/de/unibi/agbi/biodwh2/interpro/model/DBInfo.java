package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DBInfo {
    @JacksonXmlProperty(localName = "version", isAttribute = true)
    public String version;
    @JacksonXmlProperty(localName = "dbname", isAttribute = true)
    public String dbName;
    @JacksonXmlProperty(localName = "entry_count", isAttribute = true)
    public Integer entryCount;
    @JacksonXmlProperty(localName = "file_date", isAttribute = true)
    public String fileDate;
}

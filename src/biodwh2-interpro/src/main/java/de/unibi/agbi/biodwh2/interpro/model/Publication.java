package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Publication {
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    public String title;
    public String journal;
    public Integer year;
    public Location location;
    public String url;
    @JacksonXmlProperty(localName = "doi_url")
    public String doiUrl;
    @JacksonXmlProperty(localName = "author_list")
    public String authorList;
    @JacksonXmlProperty(localName = "book_title")
    public String bookTitle;
    @JacksonXmlProperty(localName = "db_xref")
    public DBXref dbXref;
}

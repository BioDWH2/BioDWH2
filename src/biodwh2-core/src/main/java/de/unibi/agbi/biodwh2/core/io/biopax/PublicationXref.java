package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class PublicationXref extends Xref {
    @JacksonXmlElementWrapper(useWrapping = false)
    @GraphProperty("url")
    public String[] url;
    @JacksonXmlElementWrapper(useWrapping = false)
    @GraphProperty("source")
    public String[] source;
    @JacksonXmlElementWrapper(useWrapping = false)
    @GraphProperty("author")
    public String[] author;
    @GraphProperty("title")
    public String title;
    @GraphProperty("year")
    public Integer year;
}

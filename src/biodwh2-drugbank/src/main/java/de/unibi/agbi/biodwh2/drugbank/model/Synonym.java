package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("Synonym")
public final class Synonym {
    @JacksonXmlProperty(isAttribute = true)
    @GraphProperty("language")
    public String language;
    @JacksonXmlProperty(isAttribute = true)
    @GraphProperty(value = "coder", ignoreEmpty = true)
    public String coder;
    @JacksonXmlText
    @GraphProperty("name")
    public String value;
}

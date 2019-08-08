package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public final class Organism {
    @JacksonXmlText
    public String value;
    @JsonProperty("ncbi-taxonomy-id")
    public String ncbiTaxonomyId;
}

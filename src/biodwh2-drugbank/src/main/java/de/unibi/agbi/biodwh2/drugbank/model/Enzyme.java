package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public final class Enzyme extends Interactant {
    @JacksonXmlProperty(isAttribute = true)
    public int position;
    @JsonProperty("inhibition-strength")
    public String inhibitionStrength;
    @JsonProperty("induction-strength")
    public String inductionStrength;
}

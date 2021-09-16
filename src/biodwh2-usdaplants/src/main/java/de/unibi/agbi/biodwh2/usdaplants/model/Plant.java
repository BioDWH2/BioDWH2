package de.unibi.agbi.biodwh2.usdaplants.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Symbol", "Synonym Symbol", "Scientific Name with Author", "Common Name", "Family"})
public class Plant {
    @JsonProperty("Symbol")
    public String symbol;
    @JsonProperty("Synonym Symbol")
    public String synonymSymbol;
    @JsonProperty("Scientific Name with Author")
    public String scientificNameWithAuthor;
    @JsonProperty("Common Name")
    public String commonName;
    @JsonProperty("Family")
    public String family;
}

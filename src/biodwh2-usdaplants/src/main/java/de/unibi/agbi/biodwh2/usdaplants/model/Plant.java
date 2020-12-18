package de.unibi.agbi.biodwh2.usdaplants.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@JsonPropertyOrder({"Symbol", "Synonym Symbol", "Scientific Name with Author", "Common Name", "Family"})
@NodeLabels("Plant")
public class Plant {
    @JsonProperty("Symbol")
    @GraphProperty("symbol")
    public String symbol;
    @JsonProperty("Synonym Symbol")
    @GraphProperty("synonym_symbol")
    public String synonymSymbol;
    @JsonProperty("Scientific Name with Author")
    @GraphProperty("scientific_name_with_author")
    public String scientificNameWithAuthor;
    @JsonProperty("Common Name")
    @GraphProperty("common_name")
    public String commonName;
    @JsonProperty("Family")
    @GraphProperty("family")
    public String family;
}

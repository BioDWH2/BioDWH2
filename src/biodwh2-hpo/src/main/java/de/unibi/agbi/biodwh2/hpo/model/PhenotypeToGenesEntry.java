package de.unibi.agbi.biodwh2.hpo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "HPO-id", "HPO label", "entrez-gene-id", "entrez-gene-symbol", "Additional Info from G-D source", "G-D source",
        "disease-ID for link"
})
public final class PhenotypeToGenesEntry {
    @JsonProperty("HPO-id")
    public String hpoId;
    @JsonProperty("HPO label")
    public String hpoLabel;
    @JsonProperty("entrez-gene-id")
    public Integer entrezGeneId;
    @JsonProperty("entrez-gene-symbol")
    public String entrezGeneSymbol;
    @JsonProperty("Additional Info from G-D source")
    public String additionalInfoFromGDSource;
    @JsonProperty("G-D source")
    public String gdSource;
    @JsonProperty("disease-ID for link")
    public String diseaseIdForLink;
}

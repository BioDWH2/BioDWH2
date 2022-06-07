package de.unibi.agbi.biodwh2.tissues.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"gene identifier", "gene name", "tissue identifier", "tissue name", "confidence score"})
public class IntegratedEntry {
    @JsonProperty("gene identifier")
    public String geneIdentifier;
    @JsonProperty("gene name")
    public String geneName;
    @JsonProperty("tissue identifier")
    public String tissueIdentifier;
    @JsonProperty("tissue name")
    public String tissueName;
    @JsonProperty("confidence score")
    public String confidenceScore;
}

package de.unibi.agbi.biodwh2.tissues.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "gene identifier", "gene name", "tissue identifier", "tissue name", "source dataset", "expression score",
        "confidence score"
})
public class ExperimentEntry {
    @JsonProperty("gene identifier")
    public String geneIdentifier;
    @JsonProperty("gene name")
    public String geneName;
    @JsonProperty("tissue identifier")
    public String tissueIdentifier;
    @JsonProperty("tissue name")
    public String tissueName;
    @JsonProperty("source dataset")
    public String sourceDataset;
    @JsonProperty("expression score")
    public String expressionScore;
    @JsonProperty("confidence score")
    public String confidenceScore;
}

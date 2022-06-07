package de.unibi.agbi.biodwh2.tissues.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "gene identifier", "gene name", "tissue identifier", "tissue name", "source database", "evidence type",
        "confidence score"
})
public class KnowledgeEntry {
    @JsonProperty("gene identifier")
    public String geneIdentifier;
    @JsonProperty("gene name")
    public String geneName;
    @JsonProperty("tissue identifier")
    public String tissueIdentifier;
    @JsonProperty("tissue name")
    public String tissueName;
    @JsonProperty("source database")
    public String sourceDatabase;
    @JsonProperty("evidence type")
    public String evidenceType;
    @JsonProperty("confidence score")
    public String confidenceScore;
}

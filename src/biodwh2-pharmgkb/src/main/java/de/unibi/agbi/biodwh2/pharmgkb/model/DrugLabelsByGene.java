package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Gene ID", "Gene Symbol", "Label IDs", "Label Names"
})
public class DrugLabelsByGene {
    @JsonProperty("Gene ID")
    public String geneId;
    @JsonProperty("Gene Symbol")
    public String geneSymbol;
    @JsonProperty("Label IDs")
    public String labelIds;
    @JsonProperty("Label Names")
    public String labelNames;
}

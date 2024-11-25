package de.unibi.agbi.biodwh2.edk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Edited Gene ID", "Gene Symbol", "#Editing Sites", "Disease", "Editing Type", "Enzyme", "Editing Level",
        "Correlation"
})
public class GeneAssociation {
    @JsonProperty("Edited Gene ID")
    public String editedGeneId;
    @JsonProperty("Gene Symbol")
    public String geneSymbol;
    @JsonProperty("#Editing Sites")
    public String numEditingSites;
    @JsonProperty("Disease")
    public String disease;
    @JsonProperty("Editing Type")
    public String editingType;
    @JsonProperty("Enzyme")
    public String enzyme;
    @JsonProperty("Editing Level")
    public String editingLevel;
    @JsonProperty("Correlation")
    public String correlation;
}

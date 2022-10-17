package de.unibi.agbi.biodwh2.tarbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "geneId", "geneName", "mirna", "species", "cell_line", "tissue", "category", "method", "positive_negative",
        "direct_indirect", "up_down", "condition"
})
public class Entry {
    @JsonProperty("geneId")
    public String geneId;
    @JsonProperty("geneName")
    public String geneName;
    @JsonProperty("mirna")
    public String mirna;
    @JsonProperty("species")
    public String species;
    @JsonProperty("cell_line")
    public String cellLine;
    @JsonProperty("tissue")
    public String tissue;
    @JsonProperty("category")
    public String category;
    @JsonProperty("method")
    public String method;
    @JsonProperty("positive_negative")
    public String positiveNegative;
    @JsonProperty("direct_indirect")
    public String directIndirect;
    @JsonProperty("up_down")
    public String upDown;
    @JsonProperty("condition")
    public String condition;
}

package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"hprd_id", "refseq_id", "geneSymbol", "expression_term", "status", "reference_id"})
public class TissueExpression {
    @JsonProperty("hprd_id")
    public String hprdId;
    @JsonProperty("refseq_id")
    public String refSeqId;
    @JsonProperty("geneSymbol")
    public String geneSymbol;
    @JsonProperty("expression_term")
    public String expressionTerm;
    @JsonProperty("status")
    public String status;
    @JsonProperty("reference_id")
    public String referenceId;
}

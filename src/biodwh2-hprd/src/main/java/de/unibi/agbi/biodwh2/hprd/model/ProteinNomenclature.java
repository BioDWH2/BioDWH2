package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"hprd_id", "geneSymbol", "refseq_id", "all_nomenclature"})
public class ProteinNomenclature {
    @JsonProperty("hprd_id")
    public String hprdId;
    @JsonProperty("geneSymbol")
    public String geneSymbol;
    @JsonProperty("refseq_id")
    public String refSeqId;
    @JsonProperty("all_nomenclature")
    public String allNomenclature;
}

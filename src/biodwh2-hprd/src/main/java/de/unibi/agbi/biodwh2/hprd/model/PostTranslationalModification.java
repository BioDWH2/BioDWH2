package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "substrate_hprd_id", "substrate_gene_symbol", "substrate_isoform_id", "substrate_refseq_id", "site", "residue",
        "enzyme_name", "enzyme_hprd_id", "modification_type", "experiment_type", "reference_id"
})
public class PostTranslationalModification {
    @JsonProperty("substrate_hprd_id")
    public String substrateHprdId;
    @JsonProperty("substrate_gene_symbol")
    public String substrateGeneSymbol;
    @JsonProperty("substrate_isoform_id")
    public String substrateIsoformId;
    @JsonProperty("substrate_refseq_id")
    public String substrateRefSeqId;
    @JsonProperty("site")
    public String site;
    @JsonProperty("residue")
    public String residue;
    @JsonProperty("enzyme_name")
    public String enzymeName;
    @JsonProperty("enzyme_hprd_id")
    public String enzymeHprdId;
    @JsonProperty("modification_type")
    public String modificationType;
    @JsonProperty("experiment_type")
    public String experimentType;
    @JsonProperty("reference_id")
    public String referenceId;
}

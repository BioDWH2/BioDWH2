package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "hprd_id", "isoform_id", "refseq_id", "geneSymbol", "architecture_name", "architecture_type", "start_site",
        "end_site", "reference_type", "reference_id", "<overflow1>", "<overflow2>"
})
public class ProteinArchitecture {
    @JsonProperty("hprd_id")
    public String hprdId;
    @JsonProperty("isoform_id")
    public String isoformId;
    @JsonProperty("refseq_id")
    public String refSeqId;
    @JsonProperty("geneSymbol")
    public String geneSymbol;
    @JsonProperty("architecture_name")
    public String architectureName;
    @JsonProperty("architecture_type")
    public String architectureType;
    @JsonProperty("start_site")
    public Integer startSite;
    @JsonProperty("end_site")
    public String endSite;
    @JsonProperty("reference_type")
    public String referenceType;
    @JsonProperty("reference_id")
    public String referenceId;
    @JsonProperty("<overflow1>")
    public String overflowHelper1;
    @JsonProperty("<overflow2>")
    public String overflowHelper2;
}

package de.unibi.agbi.biodwh2.iptmnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "ptm_type", "source", "substrate_UniProtAC", "substrate_genename", "organism", "site", "enzyme_UniProtAC",
        "enzyme_genename", "note", "pmid"
})
public class PTM {
    @JsonProperty("ptm_type")
    public String type;
    @JsonProperty("source")
    public String source;
    @JsonProperty("substrate_UniProtAC")
    public String substrateUniProtAccession;
    @JsonProperty("substrate_genename")
    public String substrateGeneName;
    @JsonProperty("organism")
    public String organism;
    @JsonProperty("site")
    public String site;
    @JsonProperty("enzyme_UniProtAC")
    public String enzymeUniProtAccession;
    @JsonProperty("enzyme_genename")
    public String enzymeGeneName;
    @JsonProperty("note")
    public String note;
    @JsonProperty("pmid")
    public String pmids;
}

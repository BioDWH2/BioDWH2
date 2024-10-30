package de.unibi.agbi.biodwh2.ptmd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "UniProt", "Position", "Type", "State", "Disease", "MutationSite", "Source", "Residue",
        "Is_experimental_verification", "Enzyme", "Gene name", "PMID", "Sentence", "CellType", "PDAs_id"
})
public class Entry {
    @JsonProperty("UniProt")
    public String uniProt;
    @JsonProperty("Position")
    public String position;
    @JsonProperty("Type")
    public String type;
    @JsonProperty("State")
    public String state;
    @JsonProperty("Disease")
    public String disease;
    @JsonProperty("MutationSite")
    public String mutationSite;
    @JsonProperty("Source")
    public String source;
    @JsonProperty("Residue")
    public String residue;
    @JsonProperty("Is_experimental_verification")
    public Integer isExperimentalVerification;
    @JsonProperty("Enzyme")
    public String enzyme;
    @JsonProperty("Gene name")
    public String geneName;
    @JsonProperty("PMID")
    public String pmids;
    @JsonProperty("Sentence")
    public String sentence;
    @JsonProperty("CellType")
    public String cellType;
    @JsonProperty("PDAs_id")
    public String pdasId;
}

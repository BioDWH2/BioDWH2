package de.unibi.agbi.biodwh2.rnalocate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "RNALocate_ID", "Species", "RNA_Symbol", "RNA_Type", "Subcellular_Localization", "GO_Accession", "PubMed_ID",
        "RNALocate_Score"
})
public class ExperimentalEntry {
    @JsonProperty("RNALocate_ID")
    public String id;
    @JsonProperty("Species")
    public String species;
    @JsonProperty("RNA_Symbol")
    public String rnaSymbol;
    @JsonProperty("RNA_Type")
    public String rnaType;
    @JsonProperty("Subcellular_Localization")
    public String subcellularLocalization;
    @JsonProperty("GO_Accession")
    public String goAccession;
    @JsonProperty("PubMed_ID")
    public Integer pmid;
    @JsonProperty("RNALocate_Score")
    public String score;
}

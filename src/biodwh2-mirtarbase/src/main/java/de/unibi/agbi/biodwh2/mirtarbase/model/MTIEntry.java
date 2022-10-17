package de.unibi.agbi.biodwh2.mirtarbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "miRTarBase ID", "miRNA", "Species (miRNA)", "Target Gene", "Target Gene (Entrez ID)", "Species (Target Gene)",
        "Experiments", "Support Type", "References (PMID)"
})
public class MTIEntry {
    @JsonProperty("miRTarBase ID")
    public String miRTarBaseId;
    @JsonProperty("miRNA")
    public String miRNA;
    @JsonProperty("Species (miRNA)")
    public String speciesMiRNA;
    @JsonProperty("Target Gene")
    public String targetGene;
    @JsonProperty("Target Gene (Entrez ID)")
    public Long targetGeneEntrezId;
    @JsonProperty("Species (Target Gene)")
    public String speciesTargetGene;
    @JsonProperty("Experiments")
    public String experiments;
    @JsonProperty("Support Type")
    public String supportType;
    @JsonProperty("References (PMID)")
    public String references;
}

package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Regulation Type", "Combination ID ", "Mechanism ID", "Regulation Result", "Regulation Form", "Molecule ID",
        "Kegg PaDthway I", "Other pathway", "Biologica_Regulation"
})
public class MoleculeRegulationTypeAndInfo {
    @JsonProperty("Regulation Type")
    public String regulationType;
    @JsonProperty("Combination ID ")
    public String combinationId;
    @JsonProperty("Mechanism ID")
    public String mechanismID;
    @JsonProperty("Regulation Result")
    public String regulationResult;
    @JsonProperty("Regulation Form")
    public String regulationForm;
    @JsonProperty("Molecule ID")
    public String moleculeId;
    @JsonProperty("Kegg PaDthway I")
    public String keggPathwayI;
    @JsonProperty("Other pathway")
    public String otherPathway;
    @JsonProperty("Biologica_Regulation")
    public String biologicaRegulation;
}

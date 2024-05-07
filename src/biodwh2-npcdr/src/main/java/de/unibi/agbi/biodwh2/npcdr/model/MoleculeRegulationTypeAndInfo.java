package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Regulation Type", "Combination ID ", "Mechanism ID", "Regulation Result", "Regulation Form", "Molecule ID",
        "Kegg PaDthway I", "Other pathway", "Biologica_Regulation"
})
public class MoleculeRegulationTypeAndInfo {
    @JsonProperty("Regulation Type")
    @GraphProperty(value = "regulation_type", emptyPlaceholder = ".")
    public String regulationType;
    @JsonProperty("Combination ID ")
    public String combinationId;
    @JsonProperty("Mechanism ID")
    public String mechanismId;
    @JsonProperty("Regulation Result")
    @GraphProperty(value = "regulation_result", emptyPlaceholder = ".")
    public String regulationResult;
    @JsonProperty("Regulation Form")
    @GraphProperty(value = "regulation_form", emptyPlaceholder = ".")
    public String regulationForm;
    @JsonProperty("Molecule ID")
    public String moleculeId;
    @JsonProperty("Kegg PaDthway I")
    @GraphProperty(value = "kegg_pathway", emptyPlaceholder = ".")
    public String keggPathwayI;
    @JsonProperty("Other pathway")
    @GraphProperty(value = "other_pathway", emptyPlaceholder = ".")
    public String otherPathway;
    @JsonProperty("Biologica_Regulation")
    @GraphProperty(value = "biologica_regulation", emptyPlaceholder = ".")
    public String biologicaRegulation;
}

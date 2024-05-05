package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Combination ID", "Disease ID", "Cell Line ID", "In Vivo", "Combination Effect Level 1",
        "Combination Effect Level 2", "Conclusions", "Mechanism ID"
})
public class CombinationEffectAndExperimentModel {
    @JsonProperty("Combination ID")
    public String combinationId;
    @JsonProperty("Disease ID")
    public String diseaseId;
    @JsonProperty("Cell Line ID")
    public String cellLineId;
    @JsonProperty("In Vivo")
    public String inVivo;
    @JsonProperty("Combination Effect Level 1")
    public String combinationEffectLevel1;
    @JsonProperty("Combination Effect Level 2")
    public String combinationEffectLevel2;
    @JsonProperty("Conclusions")
    public String conclusions;
    @JsonProperty("Mechanism ID")
    public String mechanismId;
}

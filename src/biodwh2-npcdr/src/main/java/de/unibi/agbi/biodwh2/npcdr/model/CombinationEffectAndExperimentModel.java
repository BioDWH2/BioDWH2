package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.npcdr.etl.NPCDRGraphExporter;

@JsonPropertyOrder({
        "Combination ID", "Disease ID", "Cell Line ID", "In Vivo", "Combination Effect Level 1",
        "Combination Effect Level 2", "Conclusions", "Mechanism ID"
})
@GraphNodeLabel(NPCDRGraphExporter.MECHANISM_LABEL)
public class CombinationEffectAndExperimentModel {
    @JsonProperty("Combination ID")
    public String combinationId;
    @JsonProperty("Disease ID")
    public String diseaseId;
    @JsonProperty("Cell Line ID")
    public String cellLineId;
    @JsonProperty("In Vivo")
    @GraphProperty(value = "in_vivo", emptyPlaceholder = ".")
    public String inVivo;
    @JsonProperty("Combination Effect Level 1")
    @GraphProperty(value = "combination_effect_level_1", emptyPlaceholder = ".")
    public String combinationEffectLevel1;
    @JsonProperty("Combination Effect Level 2")
    @GraphProperty(value = "combination_effect_level_2", emptyPlaceholder = ".")
    public String combinationEffectLevel2;
    @JsonProperty("Conclusions")
    @GraphProperty(value = "conclusions", emptyPlaceholder = ".")
    public String conclusions;
    @JsonProperty("Mechanism ID")
    @GraphProperty(value = GraphExporter.ID_KEY, emptyPlaceholder = ".")
    public String mechanismId;
}

package de.unibi.agbi.biodwh2.pathwaycommons.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "PARTICIPANT_A", "INTERACTION_TYPE", "PARTICIPANT_B", "INTERACTION_DATA_SOURCE", "INTERACTION_PUBMED_ID",
        "PATHWAY_NAMES", "MEDIATOR_IDS"
})
public class InteractionEntry {
    @JsonProperty("PARTICIPANT_A")
    public String participantA;
    @JsonProperty("INTERACTION_TYPE")
    public String type;
    @JsonProperty("PARTICIPANT_B")
    public String participantB;
    @JsonProperty("INTERACTION_DATA_SOURCE")
    public String dataSources;
    @JsonProperty("INTERACTION_PUBMED_ID")
    public String pubmedIds;
    @JsonProperty("PATHWAY_NAMES")
    public String pathwayNames;
    @JsonProperty("MEDIATOR_IDS")
    public String mediatorIds;
}

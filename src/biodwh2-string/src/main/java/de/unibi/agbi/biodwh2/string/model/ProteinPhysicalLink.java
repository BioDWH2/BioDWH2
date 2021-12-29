package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphEdgeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "protein1", "protein2", "homology", "experiments", "experiments_transferred", "database",
        "database_transferred", "textmining", "textmining_transferred", "combined_score"
})
@GraphEdgeLabel("PHYSICALLY_INTERACTS_WITH")
public class ProteinPhysicalLink {
    @JsonProperty("protein1")
    public String protein1;
    @JsonProperty("protein2")
    public String protein2;
    @JsonProperty("homology")
    @GraphProperty("homology")
    public Integer homology;
    @JsonProperty("experiments")
    @GraphProperty("experiments")
    public Integer experiments;
    @JsonProperty("experiments_transferred")
    @GraphProperty("experiments_transferred")
    public Integer experimentsTransferred;
    @JsonProperty("database")
    @GraphProperty("database")
    public Integer database;
    @JsonProperty("database_transferred")
    @GraphProperty("database_transferred")
    public Integer databaseTransferred;
    @JsonProperty("textmining")
    @GraphProperty("textmining")
    public Integer textmining;
    @JsonProperty("textmining_transferred")
    @GraphProperty("textmining_transferred")
    public Integer textminingTransferred;
    @JsonProperty("combined_score")
    @GraphProperty("combined_score")
    public Integer combinedScore;
}

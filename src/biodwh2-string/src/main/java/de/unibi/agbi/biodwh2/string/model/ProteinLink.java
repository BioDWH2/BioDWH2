package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphEdgeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "protein1", "protein2", "neighborhood", "neighborhood_transferred", "fusion", "cooccurence", "homology",
        "coexpression", "coexpression_transferred", "experiments", "experiments_transferred", "database",
        "database_transferred", "textmining", "textmining_transferred", "combined_score"
})
@GraphEdgeLabel("ASSOCIATED_WITH")
public class ProteinLink {
    @JsonProperty("protein1")
    public String protein1;
    @JsonProperty("protein2")
    public String protein2;
    @JsonProperty("neighborhood")
    @GraphProperty("neighborhood")
    public Integer neighborhood;
    @JsonProperty("neighborhood_transferred")
    @GraphProperty("neighborhood_transferred")
    public Integer neighborhoodTransferred;
    @JsonProperty("fusion")
    @GraphProperty("fusion")
    public Integer fusion;
    @JsonProperty("cooccurence")
    @GraphProperty("cooccurence")
    public Integer cooccurence;
    @JsonProperty("homology")
    @GraphProperty("homology")
    public Integer homology;
    @JsonProperty("coexpression")
    @GraphProperty("coexpression")
    public Integer coexpression;
    @JsonProperty("coexpression_transferred")
    @GraphProperty("coexpression_transferred")
    public Integer coexpressionTransferred;
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

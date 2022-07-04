package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"disease_id", "name", "description", "type", "name_vector", "description_vector"})
@GraphNodeLabel("Disease")
public class Disease {
    @JsonProperty("disease_id")
    @GraphProperty("id")
    public Long diseaseId;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("name_vector")
    public String nameVector;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("description_vector")
    public String descriptionVector;
}

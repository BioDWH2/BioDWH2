package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "go_process_id", "term", "definition", "last_modified", "annotation", "go_id", "term_vector",
        "definition_vector", "go_id_vector"
})
@GraphNodeLabel("GoProcess")
public class GoProcess {
    @JsonProperty("go_process_id")
    @GraphProperty("id")
    public Long goProcessId;
    @JsonProperty("term")
    @GraphProperty("term")
    public String term;
    @JsonProperty("definition")
    @GraphProperty("definition")
    public String definition;
    @JsonProperty("last_modified")
    @GraphProperty("last_modified")
    public String lastModified;
    @JsonProperty("annotation")
    @GraphProperty("annotation")
    public String annotation;
    @JsonProperty("go_id")
    @GraphProperty("go_id")
    public String goId;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("term_vector")
    public String termVector;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("definition_vector")
    public String definitionVector;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("go_id_vector")
    public String goIdVector;
}

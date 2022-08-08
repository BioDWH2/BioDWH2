package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "family_id", "name", "last_modified", "old_family_id", "type", "display_order", "annotation_status",
        "previous_names", "only_grac", "only_iuphar", "in_cgtp", "cite_id", "name_vector", "previous_names_vector"
})
@GraphNodeLabel("Family")
public class Family {
    @JsonProperty("family_id")
    @GraphProperty("id")
    public Long id;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("last_modified")
    @GraphProperty("last_modified")
    public String lastModified;
    @JsonProperty("old_family_id")
    @GraphProperty("old_id")
    public String oldId;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("display_order")
    public String displayOrder;
    @JsonProperty("annotation_status")
    @GraphProperty("annotation_status")
    public String annotationStatus;
    @JsonProperty("previous_names")
    @GraphProperty("previous_names")
    public String previousNames;
    @JsonProperty("only_grac")
    @GraphBooleanProperty(value = "only_grac", truthValue = "t")
    public String onlyGrac;
    @JsonProperty("only_iuphar")
    @GraphBooleanProperty(value = "only_iuphar", truthValue = "t")
    public String onlyIuphar;
    @JsonProperty("in_cgtp")
    @GraphBooleanProperty(value = "in_cgtp", truthValue = "t")
    public String inCgtp;
    @JsonProperty("cite_id")
    public String citeId;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("name_vector")
    public String nameVector;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("previous_names_vector")
    public String previousNamesVector;
}

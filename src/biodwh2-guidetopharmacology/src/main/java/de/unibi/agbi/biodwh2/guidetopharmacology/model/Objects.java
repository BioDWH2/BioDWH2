package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "object_id", "name", "last_modified", "comments", "structural_info_comments", "old_object_id",
        "annotation_status", "only_iuphar", "grac_comments", "only_grac", "no_contributor_list", "abbreviation",
        "systematic_name", "quaternary_structure_comments", "in_cgtp", "in_gtip", "gtip_comment", "in_gtmp",
        "gtmp_comment", "cite_id"
})
@GraphNodeLabel("Object")
public class Objects {
    @JsonProperty("object_id")
    @GraphProperty("id")
    public Long objectId;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("last_modified")
    @GraphProperty("last_modified")
    public String lastModified;
    @JsonProperty("comments")
    @GraphProperty("comments")
    public String comments;
    @JsonProperty("structural_info_comments")
    @GraphProperty("structural_info_comments")
    public String structuralInfoComments;
    @JsonProperty("old_object_id")
    @GraphProperty("old_id")
    public Long oldObjectId;
    @JsonProperty("annotation_status")
    @GraphProperty("annotation_status")
    public Long annotationStatus;
    @JsonProperty("only_iuphar")
    @GraphBooleanProperty(value = "only_iuphar", truthValue = "t")
    public String onlyIuphar;
    @JsonProperty("grac_comments")
    @GraphProperty("grac_comments")
    public String gracComments;
    @JsonProperty("only_grac")
    @GraphBooleanProperty(value = "only_grac", truthValue = "t")
    public String onlyGrac;
    @JsonProperty("no_contributor_list")
    @GraphBooleanProperty(value = "no_contributor_list", truthValue = "t")
    public String noContributorList;
    @JsonProperty("abbreviation")
    @GraphProperty("abbreviation")
    public String abbreviation;
    @JsonProperty("systematic_name")
    @GraphProperty("systematic_name")
    public String systematicName;
    @JsonProperty("quaternary_structure_comments")
    @GraphProperty("quaternary_structure_comments")
    public String quaternaryStructureComments;
    @JsonProperty("in_cgtp")
    @GraphBooleanProperty(value = "in_cgtp", truthValue = "t")
    public String inCgtp;
    @JsonProperty("in_gtip")
    @GraphBooleanProperty(value = "in_gtip", truthValue = "t")
    public String inGtip;
    @JsonProperty("gtip_comment")
    @GraphProperty("gtip_comment")
    public String gtipComment;
    @JsonProperty("in_gtmp")
    @GraphBooleanProperty(value = "in_gtmp", truthValue = "t")
    public String inGtmp;
    @JsonProperty("gtmp_comment")
    @GraphProperty("gtmp_comment")
    public String gtmpComment;
    @JsonProperty("cite_id")
    public String citeId;
}

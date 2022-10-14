package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "act_id", "struct_id", "target_id", "target_name", "target_class", "accession", "gene", "swissprot",
        "act_value", "act_unit", "act_type", "act_comment", "act_source", "relation", "moa", "moa_source",
        "act_source_url", "moa_source_url", "action_type", "first_in_class", "tdl", "act_ref_id", "moa_ref_id",
        "organism"
})
@GraphNodeLabel("Bioactivity")
public final class ActTableFull {
    @JsonProperty("act_id")
    public Integer actId;
    @JsonProperty("struct_id")
    public Long structId;
    @JsonProperty("target_id")
    public Integer targetId;
    @JsonProperty("target_name")
    @GraphProperty("target_name")
    public String targetName;
    @JsonProperty("target_class")
    @GraphProperty("target_class")
    public String targetClass;
    @JsonProperty("accession")
    @GraphProperty("accession")
    public String accession;
    @JsonProperty("gene")
    @GraphProperty("gene")
    public String gene;
    @JsonProperty("swissprot")
    @GraphProperty("swissprot")
    public String swissProt;
    @JsonProperty("act_value")
    @GraphProperty("act_value")
    public String actValue;
    @JsonProperty("act_unit")
    @GraphProperty("act_unit")
    public String actUnit;
    @JsonProperty("act_type")
    @GraphProperty("act_type")
    public String actType;
    @JsonProperty("act_comment")
    @GraphProperty("act_comment")
    public String actComment;
    @JsonProperty("act_source")
    @GraphProperty("act_source")
    public String actSource;
    @JsonProperty("relation")
    @GraphProperty("relation")
    public String relation;
    @JsonProperty("moa")
    @GraphProperty("moa")
    public String moa;
    @JsonProperty("moa_source")
    @GraphProperty("moa_source")
    public String moaSource;
    @JsonProperty("act_source_url")
    @GraphProperty("act_source_url")
    public String actSourceUrl;
    @JsonProperty("moa_source_url")
    @GraphProperty("moa_source_url")
    public String moaSourceUrl;
    @JsonProperty("action_type")
    public String actionType;
    @JsonProperty("first_in_class")
    @GraphProperty("first_in_class")
    public String firstInClass;
    @JsonProperty("tdl")
    @GraphProperty("tdl")
    public String tdl;
    @JsonProperty("act_ref_id")
    public String actRefId;
    @JsonProperty("moa_ref_id")
    public String moaRefId;
    @JsonProperty("organism")
    @GraphProperty("organism")
    public String organism;
}

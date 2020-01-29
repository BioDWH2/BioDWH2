package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "act_id", "struct_id", "target_id", "target_name", "target_class", "accession", "gene", "swissprot",
        "act_value", "act_unit", "act_type", "act_comment", "act_source", "relation", "moa", "moa_source",
        "act_source_url", "moa_source_url", "action_type", "first_in_class", "tdl", "act_ref_id", "moa_ref_id",
        "organism"
})

public final class ActTableFull {
    @JsonProperty("act_id")
    public String actId;
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("target_id")
    public String targetId;
    @JsonProperty("target_name")
    public String targetName;
    @JsonProperty("target_class")
    public String targetClass;
    @JsonProperty("accession")
    public String accession;
    @JsonProperty("gene")
    public String gene;
    @JsonProperty("swissprot")
    public String swissprot;
    @JsonProperty("act_value")
    public String actValue;
    @JsonProperty("act_unit")
    public String actUnit;
    @JsonProperty("act_type")
    public String actType;
    @JsonProperty("act_comment")
    public String actComment;
    @JsonProperty("act_source")
    public String actSource;
    @JsonProperty("relation")
    public String relation;
    @JsonProperty("moa")
    public String moa;
    @JsonProperty("moa_source")
    public String moaSource;
    @JsonProperty("act_source_url")
    public String actSourceUrl;
    @JsonProperty("moa_source_url")
    public String moaSourceUrl;
    @JsonProperty("action_type")
    public String actionType;
    @JsonProperty("first_in_class")
    public String firstInClass;
    @JsonProperty("tdl")
    public String tdl;
    @JsonProperty("act_ref_id")
    public String actRefId;
    @JsonProperty("moa_ref_id")
    public String moaRefId;
    @JsonProperty("organism")
    public String organism;
}

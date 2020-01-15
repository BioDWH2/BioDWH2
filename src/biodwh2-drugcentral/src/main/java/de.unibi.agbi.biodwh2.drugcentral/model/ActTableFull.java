package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "actId", "structId", "targetId", "targetName", "targetClass", "accession", "gene", "swissprot", "actValue",
        "actUnit", "actType", "actComment", "actSource", "relation", "moa", "moaSource", "actSourceUrl", "moaSourceUrl",
        "actionType", "firstInClass", "tdl", "actRefId", "moaRefId", "organism"
})

public final class ActTableFull {
    @JsonProperty("actId")
    public String actId;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("targetId")
    public String targetId;
    @JsonProperty("targetName")
    public String targetName;
    @JsonProperty("targetClass")
    public String targetClass;
    @JsonProperty("accession")
    public String accession;
    @JsonProperty("gene")
    public String gene;
    @JsonProperty("swissprot")
    public String swissprot;
    @JsonProperty("actValue")
    public String actValue;
    @JsonProperty("actUnit")
    public String actUnit;
    @JsonProperty("actType")
    public String actType;
    @JsonProperty("actComment")
    public String actComment;
    @JsonProperty("actSource")
    public String actSource;
    @JsonProperty("relation")
    public String relation;
    @JsonProperty("moa")
    public String moa;
    @JsonProperty("moaSource")
    public String moaSource;
    @JsonProperty("actSourceUrl")
    public String actSourceUrl;
    @JsonProperty("moaSourceUrl")
    public String moaSourceUrl;
    @JsonProperty("actionType")
    public String actionType;
    @JsonProperty("firstInClass")
    public String firstInClass;
    @JsonProperty("tdl")
    public String tdl;
    @JsonProperty("actRefId")
    public String actRefId;
    @JsonProperty("moaRefId")
    public String moaRefId;
    @JsonProperty("organism")
    public String organism;
}

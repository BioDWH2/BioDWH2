package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseGraphExporter;

@JsonPropertyOrder({
        "auto_mature", "mature_name", "previous_mature_id", "mature_acc", "evidence", "experiment", "similarity",
        "dead_flag"
})
@GraphNodeLabel(MiRBaseGraphExporter.MI_RNA_LABEL)
public class MirnaMature {
    @JsonProperty("auto_mature")
    public Long autoId;
    @JsonProperty("mature_name")
    @GraphProperty("id")
    public String matureName;
    @JsonProperty("previous_mature_id")
    @GraphProperty("previous_id")
    public String previousMatureId;
    @JsonProperty("mature_acc")
    @GraphProperty("accession")
    public String matureAcc;
    @JsonProperty("evidence")
    @GraphProperty("evidence")
    public String evidence;
    @JsonProperty("experiment")
    @GraphProperty(value = "experiment", ignoreEmpty = true, emptyPlaceholder = "\\N")
    public String experiment;
    @JsonProperty("similarity")
    @GraphProperty("similarity")
    public String similarity;
    @JsonProperty("dead_flag")
    public Integer deadFlag;
}

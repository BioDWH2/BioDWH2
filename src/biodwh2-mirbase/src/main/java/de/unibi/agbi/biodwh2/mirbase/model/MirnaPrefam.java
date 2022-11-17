package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseGraphExporter;

@JsonPropertyOrder({"auto_prefam", "prefam_acc", "prefam_id", "description"})
@GraphNodeLabel(MiRBaseGraphExporter.FAMILY_LABEL)
public class MirnaPrefam {
    @JsonProperty("auto_prefam")
    public Long autoPrefam;
    @JsonProperty("prefam_acc")
    @GraphProperty("accession")
    public String prefamAcc;
    @JsonProperty("prefam_id")
    @GraphProperty("id")
    public String prefamId;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
}

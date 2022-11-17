package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseGraphExporter;

@JsonPropertyOrder({
        "auto_mirna", "mirna_acc", "mirna_id", "previous_mirna_id", "description", "sequence", "comment",
        "auto_species", "dead_flag"
})
@GraphNodeLabel(MiRBaseGraphExporter.PRE_MI_RNA_LABEL)
public class Mirna {
    @JsonProperty("auto_mirna")
    public Long autoId;
    @JsonProperty("mirna_acc")
    @GraphProperty("accession")
    public String mirnaAcc;
    @JsonProperty("mirna_id")
    @GraphProperty("id")
    public String mirnaId;
    @JsonProperty("previous_mirna_id")
    @GraphProperty("previous_id")
    public String previousMirnaId;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("sequence")
    @GraphProperty("sequence")
    public String sequence;
    @JsonProperty("comment")
    @GraphProperty("comment")
    public String comment;
    @JsonProperty("auto_species")
    public Long autoSpecies;
    @JsonProperty("dead_flag")
    public Integer deadFlag;
}

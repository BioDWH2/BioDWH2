package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIGraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"ID", "COMPOUND_ID", "STRUCTURE", "TYPE", "DIMENSION", "DEFAULT_STRUCTURE", "AUTOGEN_STRUCTURE"})
@GraphNodeLabel(ChEBIGraphExporter.STRUCTURE_LABEL)
public class Structure {
    @JsonProperty("ID")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("COMPOUND_ID")
    public Integer compoundId;
    @JsonProperty("STRUCTURE")
    @GraphProperty("structure")
    public String structure;
    @JsonProperty("TYPE")
    @GraphProperty("type")
    public String type;
    @JsonProperty("DIMENSION")
    @GraphProperty("dimension")
    public String dimension;
    @JsonProperty("DEFAULT_STRUCTURE")
    @GraphBooleanProperty(value = "default_structure", truthValue = "Y")
    public String defaultStructure;
    @JsonProperty("AUTOGEN_STRUCTURE")
    @GraphBooleanProperty(value = "autogen_structure", truthValue = "Y")
    public String autogenStructure;
}

package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIGraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "id", "compound_id", "status_id", "molfile", "smiles", "standard_inchi", "standard_inchi_key", "dimension",
        "default_structure"
})
@GraphNodeLabel(ChEBIGraphExporter.STRUCTURE_LABEL)
public class Structure {
    @JsonProperty("id")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("compound_id")
    public Integer compoundId;
    @JsonProperty("status_id")
    public Integer statusId;
    @JsonProperty("molfile")
    @GraphProperty("molfile")
    public String structure;
    @JsonProperty("smiles")
    @GraphProperty("smiles")
    public String smiles;
    @JsonProperty("standard_inchi")
    @GraphProperty("standard_inchi")
    public String standardInchi;
    @JsonProperty("standard_inchi_key")
    @GraphProperty("standard_inchi_key")
    public String standardInchiKey;
    @JsonProperty("dimension")
    @GraphProperty("dimension")
    public String dimension;
    @JsonProperty("default_structure")
    @GraphBooleanProperty(value = "default_structure", truthValue = "Y")
    public String defaultStructure;
}

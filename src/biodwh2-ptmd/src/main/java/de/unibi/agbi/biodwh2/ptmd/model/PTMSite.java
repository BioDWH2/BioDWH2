package de.unibi.agbi.biodwh2.ptmd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.ptmd.etl.PTMDGraphExporter;

@JsonPropertyOrder({"Uniprot ID", "Sites", "Peptide", "Source"})
@GraphNodeLabel(PTMDGraphExporter.PTM_LABEL)
public class PTMSite {
    @JsonProperty("Uniprot ID")
    public String uniprotId;
    @JsonProperty("Sites")
    @GraphProperty(value = "position")
    public String position;
    @JsonProperty("Peptide")
    @GraphProperty(value = "peptide")
    public String peptide;
    @JsonProperty("Source")
    @GraphProperty(value = "source")
    public String source;
}

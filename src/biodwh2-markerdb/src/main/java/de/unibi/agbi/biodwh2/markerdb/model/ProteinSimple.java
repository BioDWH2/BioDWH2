package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

@GraphNodeLabel(MarkerDBGraphExporter.PROTEIN_LABEL)
public class ProteinSimple extends ConditionSimple {
    @GraphNumberProperty(GraphExporter.ID_KEY)
    public String id;
    @GraphProperty("name")
    public String name;
    @JsonProperty("gene_name")
    @GraphProperty(value = "gene_name", ignoreEmpty = true)
    public String geneName;
    @JsonProperty("uniprot_id")
    @GraphProperty(value = "uniprot_id", ignoreEmpty = true)
    public String uniprotId;
}

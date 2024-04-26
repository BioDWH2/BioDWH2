package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

@GraphNodeLabel(MarkerDBGraphExporter.GENE_LABEL)
public class GeneSimple {
    @JsonProperty("biomarker_type")
    public String biomarkerType;
    @GraphNumberProperty(GraphExporter.ID_KEY)
    public String id;
    @GraphProperty("variation")
    public String variation;
    @GraphProperty("position")
    public String position;
    @JsonProperty("external_link")
    @GraphProperty("external_link")
    public String externalLink;
    @JsonProperty("gene_symbol")
    @GraphProperty("gene_symbol")
    public String geneSymbol;
    @JsonProperty("entrez_gene_id")
    @GraphProperty("entrez_gene_id")
    public String entrezGeneId;
    public String conditions;
    @JsonProperty("indication_types")
    public String indicationTypes;
}

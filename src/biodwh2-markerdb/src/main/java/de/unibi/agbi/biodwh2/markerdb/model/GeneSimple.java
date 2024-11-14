package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

public class GeneSimple extends ConditionSimple {
    @GraphNumberProperty(GraphExporter.ID_KEY)
    public String id;
    @GraphProperty(value = "variation", ignoreEmpty = true)
    public String variation;
    @GraphProperty(value = "position", ignoreEmpty = true)
    public String position;
    @JsonProperty("external_link")
    @GraphProperty(value = "external_link", ignoreEmpty = true)
    public String externalLink;
    @JsonProperty("gene_symbol")
    @GraphProperty(value = "gene_symbol", ignoreEmpty = true)
    public String geneSymbol;
    @JsonProperty("entrez_gene_id")
    @GraphProperty(value = "entrez_gene_id", ignoreEmpty = true)
    public String entrezGeneId;
}

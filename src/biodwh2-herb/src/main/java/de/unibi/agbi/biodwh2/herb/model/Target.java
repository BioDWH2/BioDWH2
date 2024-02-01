package de.unibi.agbi.biodwh2.herb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.herb.etl.HerbGraphExporter;

@JsonPropertyOrder({
        "Target_id", "Tax_id", "Gene_id", "Gene_name", "Gene_alias", "Db_xrefs", "Chromosome", "Map_location",
        "Description", "Type_of_gene", "TTD_target_id", "TTD_target_type"
})
@GraphNodeLabel(HerbGraphExporter.TARGET_LABEL)
public class Target {
    @JsonProperty("Target_id")
    @GraphProperty("id")
    public String targetId;
    @JsonProperty("Tax_id")
    @GraphProperty("tax_id")
    public String taxId;
    @JsonProperty("Gene_id")
    @GraphProperty("gene_id")
    public String geneId;
    @JsonProperty("Gene_name")
    @GraphProperty("gene_name")
    public String geneName;
    @JsonProperty("Gene_alias")
    @GraphArrayProperty(value = "gene_alias", arrayDelimiter = "; ", emptyPlaceholder = "-")
    public String geneAlias;
    @JsonProperty("Db_xrefs")
    @GraphArrayProperty(value = "xrefs", arrayDelimiter = {"; ", "|"}, emptyPlaceholder = {"-", "dbXrefs"})
    public String dbXrefs;
    @JsonProperty("Chromosome")
    @GraphProperty("chromosome")
    public String chromosome;
    @JsonProperty("Map_location")
    @GraphProperty("map_location")
    public String mapLocation;
    @JsonProperty("Description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("Type_of_gene")
    @GraphProperty("type_of_gene")
    public String typeOfGene;
    @JsonProperty("TTD_target_id")
    @GraphProperty(value = "ttd_target_id", emptyPlaceholder = "NA")
    public String ttdTargetId;
    @JsonProperty("TTD_target_type")
    @GraphProperty(value = "ttd_target_type", emptyPlaceholder = "/")
    public String ttdTargetType;
}

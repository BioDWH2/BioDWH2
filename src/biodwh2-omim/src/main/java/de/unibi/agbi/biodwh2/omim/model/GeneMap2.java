package de.unibi.agbi.biodwh2.omim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.omim.etl.OMIMGraphExporter;

@JsonPropertyOrder({
        "Chromosom", "Genomic Position Start", "Genomic Position End", "Cyto Location", "Computed Cyto Location",
        "MIM Number", "Gene Symbols", "Gene Name", "Approved Gene Symbol", "Entrez Gene ID", "Ensembl Gene ID",
        "Comments", "Phenotypes", "Mouse Gene Symbol/ID"
})
@GraphNodeLabel(OMIMGraphExporter.GENE_LABEL)
public class GeneMap2 {
    @JsonProperty("Chromosom")
    @GraphProperty("chromosome")
    public String chromosome;
    @JsonProperty("Genomic Position Start")
    @GraphProperty("genomic_position_start")
    public String genomicPS;
    @JsonProperty("Genomic Position End")
    @GraphProperty("genomic_position_end")
    public String genomicPE;
    @JsonProperty("Cyto Location")
    @GraphProperty("cyto_location")
    public String cytoLocation;
    @JsonProperty("Computed Cyto Location")
    @GraphProperty("computed_cyto_location")
    public String computedCytoLocation;
    @JsonProperty("MIM Number")
    @GraphProperty("mim_number")
    public String mimNumber;
    @JsonProperty("Gene Symbols")
    @GraphArrayProperty(value = "gene_symbols", arrayDelimiter = ", ")
    public String geneSymbols;
    @JsonProperty("Gene Name")
    @GraphProperty("name")
    public String geneName;
    @JsonProperty("Approved Gene Symbol")
    @GraphProperty("approved_gene_symbol")
    public String approvedGeneSymbol;
    @JsonProperty("Entrez Gene ID")
    @GraphProperty("entrez_gene_id")
    public String entrezGeneID;
    @JsonProperty("Ensembl Gene ID")
    @GraphProperty("ensembl_gene_id")
    public String ensemblGeneID;
    @JsonProperty("Comments")
    @GraphProperty("comments")
    public String comments;
    @JsonProperty("Phenotypes")
    public String phenotypes;
    @JsonProperty("Mouse Gene Symbol/ID")
    @GraphProperty("mouse_gene_symbol_id")
    public String mouseGene;
}

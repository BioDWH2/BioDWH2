package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseGraphExporter;

@JsonPropertyOrder({
        "auto_id", "organism", "division", "name", "taxon_id", "taxonomy", "genome_assembly", "genome_accession",
        "ensembl_db"
})
@GraphNodeLabel(MiRBaseGraphExporter.SPECIES_LABEL)
public class MirnaSpecies {
    @JsonProperty("auto_id")
    public Long autoId;
    @JsonProperty("organism")
    @GraphProperty("organism")
    public String organism;
    @JsonProperty("division")
    @GraphProperty("division")
    public String division;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("taxon_id")
    @GraphNumberProperty(value = "ncbi_taxid", emptyPlaceholder = "\\N", ignoreEmpty = true, type = GraphNumberProperty.Type.Int)
    public String taxonId;
    @JsonProperty("taxonomy")
    @GraphArrayProperty(value = "taxonomy", arrayDelimiter = ";")
    public String taxonomy;
    @JsonProperty("genome_assembly")
    @GraphProperty("genome_assembly")
    public String genomeAssembly;
    @JsonProperty("genome_accession")
    @GraphProperty("genome_accession")
    public String genomeAccession;
    @JsonProperty("ensembl_db")
    @GraphProperty("ensembl_db")
    public String ensemblDb;
}

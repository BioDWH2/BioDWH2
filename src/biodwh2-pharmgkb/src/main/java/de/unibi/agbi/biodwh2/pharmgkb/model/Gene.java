package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("Gene")
@JsonPropertyOrder({
        "PharmGKB Accession Id", "NCBI Gene ID", "HGNC ID", "Ensembl Id", "Name", "Symbol", "Alternate Names",
        "Alternate Symbols", "Is VIP", "Has Variant Annotation", "Cross-references", "Has CPIC Dosing Guideline",
        "Chromosome", "Chromosomal Start - GRCh37", "Chromosomal Stop - GRCh37", "Chromosomal Start - GRCh38",
        "Chromosomal Stop - GRCh38"
})
public class Gene {
    @JsonProperty("PharmGKB Accession Id")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @JsonProperty("NCBI Gene ID")
    @GraphArrayProperty(value = "ncbi_gene_ids", arrayDelimiter = ", ")
    public String ncbiGeneId;
    @JsonProperty("HGNC ID")
    @GraphArrayProperty(value = "hgnc_ids", arrayDelimiter = ", ")
    public String hgncId;
    @JsonProperty("Ensembl Id")
    @GraphArrayProperty(value = "ensemble_ids", arrayDelimiter = ", ")
    public String ensembleId;
    @JsonProperty("Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("Symbol")
    @GraphProperty("symbol")
    public String symbol;
    @JsonProperty("Alternate Names")
    @GraphArrayProperty(value = "alternate_names", arrayDelimiter = ", ", quotedArrayElements = true)
    public String alternateNames;
    @JsonProperty("Alternate Symbols")
    @GraphArrayProperty(value = "alternate_symbols", arrayDelimiter = ", ", quotedArrayElements = true)
    public String alternateSymbols;
    @JsonProperty("Is VIP")
    @GraphBooleanProperty(value = "is_vip", truthValue = "yes")
    public String isVip;
    @JsonProperty("Has Variant Annotation")
    @GraphBooleanProperty(value = "has_variant_annotation", truthValue = "yes")
    public String hasVariantAnnotation;
    @JsonProperty("Cross-references")
    @GraphArrayProperty(value = "cross_references", arrayDelimiter = ", ", quotedArrayElements = true)
    public String crossReference;
    @JsonProperty("Has CPIC Dosing Guideline")
    @GraphBooleanProperty(value = "has_cpic_dosing_guideline", truthValue = "yes")
    public String hasCpicDosingGuideline;
    @JsonProperty("Chromosome")
    @GraphProperty("chromosome")
    public String chromosome;
    @JsonProperty("Chromosomal Start - GRCh37")
    @GraphProperty("chromosomal_start_GRCh37")
    public String chromosomalStartGrch37;
    @JsonProperty("Chromosomal Stop - GRCh37")
    @GraphProperty("chromosomal_stop_GRCh37")
    public String chromosomalStopGrch37;
    @JsonProperty("Chromosomal Start - GRCh38")
    @GraphProperty("chromosomal_start_GRCh38")
    public String chromosomalStartGrch38;
    @JsonProperty("Chromosomal Stop - GRCh38")
    @GraphProperty("chromosomal_stop_GRCh38")
    public String chromosomalStopGrch38;
}

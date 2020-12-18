package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;
import de.unibi.agbi.biodwh2.pharmgkb.etl.PharmGKBGraphExporter;

@NodeLabels("Gene")
public class Gene {
    @Parsed(field = "PharmGKB Accession Id")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @Parsed(field = "NCBI Gene ID")
    @GraphArrayProperty(value = "ncbi_gene_ids", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String ncbiGeneId;
    @Parsed(field = "HGNC ID")
    @GraphArrayProperty(value = "hgnc_ids", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String hgncId;
    @Parsed(field = "Ensembl Id")
    @GraphArrayProperty(value = "ensemble_ids", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String ensembleId;
    @Parsed(field = "Name")
    @GraphProperty("name")
    public String name;
    @Parsed(field = "Symbol")
    @GraphProperty("symbol")
    public String symbol;
    @Parsed(field = "Alternate Names")
    @GraphArrayProperty(value = "alternate_names", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String alternateNames;
    @Parsed(field = "Alternate Symbols")
    @GraphArrayProperty(value = "alternate_symbols", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String alternateSymbols;
    @Parsed(field = "Is VIP")
    @GraphBooleanProperty(value = "is_vip", truthValue = "yes")
    public String isVip;
    @Parsed(field = "Has Variant Annotation")
    @GraphBooleanProperty(value = "has_variant_annotation", truthValue = "yes")
    public String hasVariantAnnotation;
    @Parsed(field = "Cross-references")
    @GraphArrayProperty(value = "cross_references", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String crossReference;
    @Parsed(field = "Has CPIC Dosing Guideline")
    @GraphBooleanProperty(value = "has_cpic_dosing_guideline", truthValue = "yes")
    public String hasCpicDosingGuideline;
    @Parsed(field = "Chromosome")
    @GraphProperty("chromosome")
    public String chromosome;
    @Parsed(field = "Chromosomal Start - GRCh37.p13")
    @GraphProperty("chromosomal_start_GRCh37_p13")
    public String chromosomalStartGrch37P13;
    @Parsed(field = "Chromosomal Stop - GRCh37.p13")
    @GraphProperty("chromosomal_stop_GRCh37_p13")
    public String chromosomalStopGrch37P13;
    @Parsed(field = "Chromosomal Start - GRCh38.p7")
    @GraphProperty("chromosomal_start_GRCh38_p7")
    public String chromosomalStartGrch38P7;
    @Parsed(field = "Chromosomal Stop - GRCh38.p7")
    @GraphProperty("chromosomal_stop_GRCh38_p7")
    public String chromosomalStopGrch38P7;
}

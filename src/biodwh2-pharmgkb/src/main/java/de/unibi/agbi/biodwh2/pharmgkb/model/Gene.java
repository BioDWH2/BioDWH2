package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@NodeLabel("Gene")
public class Gene {
    @Parsed(field = "PharmGKB Accession Id")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @Parsed(field = "NCBI Gene ID")
    public String ncbiGeneId;
    @Parsed(field = "HGNC ID")
    public String hgncId;
    @Parsed(field = "Ensembl Id")
    public String ensembleId;
    @Parsed(field = "Name")
    @GraphProperty("name")
    public String name;
    @Parsed(field = "Symbol")
    @GraphProperty("symbol")
    public String symbol;
    @Parsed(field = "Alternate Names")
    public String alternateNames;
    @Parsed(field = "Alternate Symbols")
    public String alternateSymbols;
    @Parsed(field = "Is VIP")
    public String isVip;
    @Parsed(field = "Has Variant Annotation")
    public String hasVariantAnnotation;
    @Parsed(field = "Cross-references")
    public String crossReference;
    @Parsed(field = "Has CPIC Dosing Guideline")
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

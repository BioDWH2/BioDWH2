package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.univocity.parsers.annotations.Parsed;

public class Gene {

    //@JsonProperty("PharmGKB Accession Id")
    @Parsed(field = "PharmGKB Accession Id")
    public String pharmgkb_accession_id;
    @Parsed(field = "NCBI Gene ID")
    public String ncbi_gene_id;
    @Parsed(field = "HGNC ID")
    public String hgnc_id;
    @Parsed(field = "Ensembl Id")
    public String ensemble_id;
    @Parsed(field = "Name")
    public String name;
    @Parsed(field = "Symbol")
    public String symbol;
    @Parsed(field = "Alternate Names")
    public String alternate_names;
    @Parsed(field = "Alternate Symbols")
    public String alternate_symbols;
    @Parsed(field = "Is VIP")
    public String is_vip;
    @Parsed(field = "Has Variant Annotation")
    public String has_variant_annotation;
    @Parsed(field = "Cross-references")
    public String cross_reference;
    @Parsed(field = "Has CPIC Dosing Guideline")
    public String has_cpic_dosing_guideline;
    @Parsed(field = "Chromosome")
    public String chromosome;
    @Parsed(field = "Chromosomal Start - GRCh37.p13")
    public String chromosomal_start_grch37_p13;
    @Parsed(field = "Chromosomal Stop - GRCh37.p13")
    public String chromosomal_stop_grch37_p13;
    @Parsed(field = "Chromosomal Start - GRCh38.p13")
    public String chromosomal_start_grch38_p13;
    @Parsed(field = "Chromosomal Stop - GRCh38.p13")
    public String chromosomal_stop_grch38_p13;
}
